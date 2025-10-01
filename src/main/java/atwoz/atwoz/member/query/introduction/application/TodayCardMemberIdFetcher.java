package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.MemberIdealCommandRepository;
import atwoz.atwoz.member.command.domain.member.Gender;
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
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodayCardMemberIdFetcher {
    private static final int COMBINATION_NONE_SELECTABLE_SIZE = 2;
    private static final long MALE_LIMIT = 3L;
    private static final long FEMALE_LIMIT = 4L;

    private final IntroductionQueryRepository introductionQueryRepository;
    private final IntroductionRedisRepository introductionRedisRepository;
    private final MemberCommandRepository memberCommandRepository;
    private final MemberIdealCommandRepository memberIdealCommandRepository;

    public Set<Long> fetch(long memberId, IntroductionCacheKeyPrefix cacheKeyPrefix) {
        String cacheKey = buildKey(cacheKeyPrefix, memberId);
        Set<Long> cachedIds = introductionRedisRepository.findIntroductionMemberIds(cacheKey);
        if (!cachedIds.isEmpty()) {
            return cachedIds;
        }
        Member member = memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        List<IntroductionSearchCondition> conditions = getConditions(member);
        Set<Long> introductionMemberIds = findAllIntroductionMemberId(conditions, member);
        introductionRedisRepository.saveIntroductionMemberIds(cacheKey, introductionMemberIds, getExpireAt());
        return introductionMemberIds;
    }

    private List<IntroductionSearchCondition> getConditions(Member member) {
        MemberIdeal memberIdeal = memberIdealCommandRepository.findByMemberId(member.getId())
            .orElseThrow(MemberIdealNotFoundException::new);
        Set<Long> excludedMemberIds = findExcludedMemberIds(member.getId());
        Gender oppositeGender = member.getGender().getOpposite();
        if (!memberIdeal.isUpdated()) {
            return List.of(
                IntroductionSearchCondition.ofTodayCardDefault(excludedMemberIds, oppositeGender, member));
        }
        final IntroductionSearchCondition baseCondition = IntroductionSearchCondition.ofIdeal(excludedMemberIds,
            memberIdeal, oppositeGender);
        return IntroductionSearchConditionCombinator.generateCombinations(baseCondition,
            COMBINATION_NONE_SELECTABLE_SIZE);
    }

    private Set<Long> findExcludedMemberIds(long memberId) {
        Set<Long> excludedMemberIds = new HashSet<>();
        excludedMemberIds.add(memberId);
        excludedMemberIds.addAll(introductionQueryRepository.findAllMatchRequestedMemberId(memberId));
        excludedMemberIds.addAll(introductionQueryRepository.findAllMatchRequestingMemberId(memberId));
        excludedMemberIds.addAll(introductionQueryRepository.findAllIntroducedMemberId(memberId));
        excludedMemberIds.addAll(introductionQueryRepository.findAllBlockedMemberId(memberId));
        excludedMemberIds.addAll(introductionQueryRepository.findAllBlockingMemberId(memberId));
        return excludedMemberIds;
    }

    private Set<Long> findAllIntroductionMemberId(List<IntroductionSearchCondition> conditions, Member member) {
        Set<Long> introductionMemberIds = new HashSet<>();
        final long limit = getLimit(member);
        for (IntroductionSearchCondition condition : conditions) {
            final long currentLimit = limit - introductionMemberIds.size();
            introductionMemberIds.addAll(
                introductionQueryRepository.findAllIntroductionMemberId(condition, currentLimit));
            if (introductionMemberIds.size() == limit) {
                break;
            }
        }
        return introductionMemberIds;
    }

    private long getLimit(Member member) {
        return member.getGender().equals(Gender.MALE) ? MALE_LIMIT : FEMALE_LIMIT;
    }

    private Date getExpireAt() {
        LocalDateTime expireDateTime = LocalDate.now().plusDays(1).atStartOfDay();
        return Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String buildKey(IntroductionCacheKeyPrefix prefix, long memberId) {
        return prefix.getPrefix() + memberId;
    }
}
