package atwoz.atwoz.member.command.domain.profileImage;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.member.command.domain.profileImage.exception.InvalidOrderException;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_images",
    indexes = {
        @Index(name = "idx_member_id_is_primary", columnList = "memberId, isPrimary")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
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

    public Integer getOrder() {
        return order;
    }

    private void setOrder(int order) {
        validateOrder(order);
        this.order = order;
    }

    public Boolean isPrimary() {
        return isPrimary;
    }

    public void updateOrderAndPrimary(int order, boolean isPrimary) {
        setOrder(order);
        setPrimary(isPrimary);
    }

    public void updateUrl(String imageUrl) {
        if (imageUrl != null) {
            setImageUrl(ImageUrl.from(imageUrl));
        }
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
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
