package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.MemberIdealCommandRepository;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.query.introduction.intra.IntroductionQueryRepository;
import atwoz.atwoz.member.query.introduction.intra.IntroductionRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IntroductionMemberIdFetcher {

    private final IntroductionQueryRepository introductionQueryRepository;
    private final IntroductionRedisRepository introductionRedisRepository;
    private final MemberCommandRepository memberCommandRepository;
    private final MemberIdealCommandRepository memberIdealCommandRepository;

    @FunctionalInterface
    public interface IntroductionConditionSupplier<T> {
        IntroductionSearchCondition get(Set<Long> excludedMemberIds, MemberIdeal memberIdeal, Member member, T criteria);
    }

    public <T> Set<Long> fetch(long memberId, IntroductionCacheKeyPrefix cacheKeyPrefix, T criteria, IntroductionConditionSupplier<T> supplier) {
        String cacheKey = buildKey(cacheKeyPrefix, memberId);
        Set<Long> cachedIds = introductionRedisRepository.findIntroductionMemberIds(cacheKey);
        if (!cachedIds.isEmpty()) {
            return cachedIds;
        }
        Set<Long> excludedMemberIds = findExcludedMemberIds(memberId);
        MemberIdeal memberIdeal = memberIdealCommandRepository.findByMemberId(memberId)
                .orElseThrow(MemberIdealNotFoundException::new);
        Member member = memberCommandRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        IntroductionSearchCondition condition = supplier.get(excludedMemberIds, memberIdeal, member, criteria);
        Set<Long> introductionMemberIds = introductionQueryRepository.findAllIntroductionMemberId(condition);
        introductionRedisRepository.saveIntroductionMemberIds(cacheKey, introductionMemberIds, getExpireAt());
        return introductionMemberIds;
    }

    private Set<Long> findExcludedMemberIds(long memberId) {
        Set<Long> excludedMemberIds = new HashSet<>();
        excludedMemberIds.add(memberId);
        excludedMemberIds.addAll(introductionQueryRepository.findAllMatchRequestedMemberId(memberId));
        excludedMemberIds.addAll(introductionQueryRepository.findAllMatchRequestingMemberId(memberId));
        excludedMemberIds.addAll(introductionQueryRepository.findAllIntroducedMemberId(memberId));
        return excludedMemberIds;
    }

    private Date getExpireAt() {
        LocalDateTime expireDateTime = LocalDate.now().plusDays(1).atStartOfDay();
        return Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String buildKey(IntroductionCacheKeyPrefix prefix, long memberId) {
        return prefix.getPrefix() + memberId;
    }
}
