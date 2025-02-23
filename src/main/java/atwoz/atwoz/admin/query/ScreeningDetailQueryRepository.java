package atwoz.atwoz.admin.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static atwoz.atwoz.admin.command.domain.screening.QScreening.screening;
import static atwoz.atwoz.interview.command.domain.answer.QInterviewAnswer.interviewAnswer;
import static atwoz.atwoz.interview.command.domain.question.QInterviewQuestion.interviewQuestion;
import static atwoz.atwoz.member.command.domain.member.QMember.member;
import static atwoz.atwoz.member.command.domain.profileImage.QProfileImage.profileImage;

@Repository
@RequiredArgsConstructor
public class ScreeningDetailQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ScreeningDetailView findById(long screeningId) {
        long version = Optional.ofNullable(
                queryFactory
                        .select(screening.version)
                        .from(screening)
                        .where(screening.id.eq(screeningId))
                        .fetchOne()
        ).orElse(-1L);

        ScreeningDetailProfileView profile = queryFactory
                .select(new QScreeningDetailProfileView(
                        member.id,
                        screening.status.stringValue(),
                        screening.rejectionReason.stringValue(),
                        member.profile.nickname.value,
                        member.profile.age,
                        member.profile.gender.stringValue(),
                        member.createdAt.stringValue()
                ))
                .from(screening)
                .join(member).on(screening.memberId.eq(member.id))
                .where(screening.id.eq(screeningId))
                .fetchOne();

        List<ScreeningDetailProfileImageView> profileImages = queryFactory
                .select(new QScreeningDetailProfileImageView(
                        profileImage.imageUrl.value,
                        profileImage.order,
                        profileImage.isPrimary
                ))
                .from(profileImage)
                .where(profileImage.memberId.eq(profile.memberId()))
                .fetch();

        List<ScreeningDetailInterviewView> interviews = queryFactory
                .select(new QScreeningDetailInterviewView(
                        interviewQuestion.content,
                        interviewAnswer.content
                ))
                .from(interviewAnswer)
                .join(interviewQuestion).on(interviewAnswer.questionId.eq(interviewQuestion.id))
                .where(interviewQuestion.isPublic.eq(true), interviewAnswer.memberId.eq(profile.memberId()))
                .fetch();

        return new ScreeningDetailView(
                screeningId,
                version,
                profile,
                profileImages,
                interviews
        );
    }
}
