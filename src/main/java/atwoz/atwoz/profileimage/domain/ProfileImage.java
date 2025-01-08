package atwoz.atwoz.profileimage.domain;

import atwoz.atwoz.common.BaseEntity;
import atwoz.atwoz.profileimage.domain.vo.ImageUrl;
import atwoz.atwoz.profileimage.exception.InvalidOrderException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Embedded
    private ImageUrl imageUrl;

    private boolean isPrimary;

    @Column(name = "profile_order")
    private int order;

    @Builder
    private ProfileImage(Long memberId, ImageUrl imageUrl, int order, boolean isPrimary) {
        setMemberId(memberId);
        setImageUrl(imageUrl);
        setOrder(order);
        setPrimary(isPrimary);
    }

    public String getUrl() {
        return imageUrl.getValue();
    }

    public Long getMemberId() {
        return memberId;
    }

    public Integer getOrder() {
        return order;
    }

    public Boolean isPrimary() {
        return isPrimary;
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setOrder(int order) {
        validateOrder(order);
        this.order = order;
    }

    private void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    private void setImageUrl(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }

    private void validateOrder(int order) {
        if (order < 0) {
            throw new InvalidOrderException();
        }
    }
}
