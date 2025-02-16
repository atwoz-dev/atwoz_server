package atwoz.atwoz.member.query.profileimage;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.query.profileimage.view.ProfileImageView;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

@Import({QuerydslConfig.class, ProfileImageQueryRepository.class})
@DataJpaTest
public class ProfileImageQueryRepositoryTest {
    @Autowired
    private ProfileImageQueryRepository profileImageQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("자신의 프로필 이미지를 조회.")
    void findMyProfileImages() {
        // Given
        Long memberId = 1L;
        ProfileImage profileImage1 = ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from("https://example.com"))
                .isPrimary(true)
                .order(1)
                .build();

        ProfileImage profileImage2 = ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from("https://example2.com"))
                .isPrimary(false)
                .order(2)
                .build();

        entityManager.persist(profileImage1);
        entityManager.persist(profileImage2);
        entityManager.flush();

        // When
        List<ProfileImageView> profileImageViews = profileImageQueryRepository.findByMemberId(memberId);

        // Then
        Assertions.assertThat(profileImageViews.size()).isEqualTo(2);

        ProfileImageView profileImageOrder1 = profileImageViews.getFirst();
        Assertions.assertThat(profileImageOrder1.id()).isEqualTo(profileImage1.getId());
        Assertions.assertThat(profileImageOrder1.url()).isEqualTo(profileImage1.getUrl());
        Assertions.assertThat(profileImageOrder1.isPrimary()).isEqualTo(profileImage1.isPrimary());
        Assertions.assertThat(profileImageOrder1.order()).isEqualTo(profileImage1.getOrder());

        ProfileImageView profileImageOrder2 = profileImageViews.getLast();
        Assertions.assertThat(profileImageOrder2.id()).isEqualTo(profileImage2.getId());
        Assertions.assertThat(profileImageOrder2.url()).isEqualTo(profileImage2.getUrl());
        Assertions.assertThat(profileImageOrder2.isPrimary()).isEqualTo(profileImage2.isPrimary());
        Assertions.assertThat(profileImageOrder2.order()).isEqualTo(profileImage2.getOrder());
    }
}
