package atwoz.atwoz.profileimage.domain;

import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.profileimage.domain.vo.ImageUrl;
import atwoz.atwoz.profileimage.domain.vo.MemberId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberId memberId;

    @Embedded
    private ImageUrl imageUrl;

    private Boolean isPrimary = false;

    public static ProfileImage of(Long memberId, String url, boolean isPrimary) {
        return ProfileImage.builder()
                .memberId(MemberId.from(memberId))
                .imageUrl(ImageUrl.from(url))
                .isPrimary(isPrimary)
                .build();
    }

    public String getUrl() {
        return imageUrl.getValue();
    }

    public Boolean isPrimary() {
        return isPrimary;
    }
}
