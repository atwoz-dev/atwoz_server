package atwoz.atwoz.profileimage.domain;

import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.profileimage.domain.vo.ImageUrl;
import atwoz.atwoz.profileimage.domain.vo.MemberId;
import atwoz.atwoz.profileimage.exception.InvalidIsPrimaryException;
import atwoz.atwoz.profileimage.exception.InvalidOrderException;
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

    @Column(name = "profile_order")
    private Integer order = null;

    public static ProfileImage of(MemberId memberId, ImageUrl url, Integer order, Boolean isPrimary) {
        validateOrder(order);
        validateIsPrimary(isPrimary);

        return ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(url)
                .order(order)
                .isPrimary(isPrimary)
                .build();
    }

    public String getUrl() {
        return imageUrl.getValue();
    }

    public Long getMemberId() {
        return memberId.getValue();
    }

    public Integer getOrder() {
        return order;
    }

    public Boolean isPrimary() {
        return isPrimary;
    }

    private static void validateOrderAndIsPrimary(Integer order, Boolean isPrimary) {
        if (order == null) {
            throw new InvalidOrderException();
        }

        if (isPrimary == null) {
            throw new InvalidIsPrimaryException();
        }
    }

    private static void validateOrder(Integer order) {
        if (order == null || order <= 0) {
            throw new InvalidOrderException();
        }
    }

    private static void validateIsPrimary(Boolean isPrimary) {
        if (isPrimary == null) {
            throw new InvalidIsPrimaryException();
        }
    }
}
