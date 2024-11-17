package awtoz.awtoz.member.domain;

public interface MemberRepository {
    Member save(Member member);

    ProfileImage saveRepProfileImage(ProfileImage profileImage); // 대표이미지 저장
}
