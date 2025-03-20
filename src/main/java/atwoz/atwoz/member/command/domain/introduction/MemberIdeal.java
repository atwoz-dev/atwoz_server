package atwoz.atwoz.member.command.domain.introduction;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.DrinkingStatus;
import atwoz.atwoz.member.command.domain.member.Region;
import atwoz.atwoz.member.command.domain.member.Religion;
import atwoz.atwoz.member.command.domain.member.SmokingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "member_ideals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberIdeal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(unique = true)
    private Long memberId;

    @Getter
    @Embedded
    private AgeRange ageRange;

    @Getter
    @ElementCollection
    @CollectionTable(name = "member_ideal_hobbies", joinColumns = @JoinColumn(name = "member_ideal_id"))
    @Column(name = "hobby_id")
    private Set<Long> hobbyIds = new HashSet<>();

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Region region;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private Religion religion;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private SmokingStatus smokingStatus;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private DrinkingStatus drinkingStatus;

    public static MemberIdeal init(Long memberId) {
        return new MemberIdeal(memberId, AgeRange.init(), new HashSet<>(), null, null, null, null);
    }

    public void update(
            AgeRange ageRange,
            Set<Long> hobbyIds,
            Region region,
            Religion religion,
            SmokingStatus smokingStatus,
            DrinkingStatus drinkingStatus
    ) {
        setAgeRange(ageRange);
        setHobbyIds(hobbyIds);
        this.region = region;
        this.religion = religion;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
    }

    private MemberIdeal(
            Long memberId,
            AgeRange ageRange,
            Set<Long> hobbyIds,
            Region region,
            Religion religion,
            SmokingStatus smokingStatus,
            DrinkingStatus drinkingStatus
    ) {
        setMemberId(memberId);
        setAgeRange(ageRange);
        setHobbyIds(hobbyIds);
        this.region = region;
        this.religion = religion;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setAgeRange(@NonNull AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    private void setHobbyIds(@NonNull Set<Long> hobbyIds) {
        this.hobbyIds = hobbyIds;
    }
}
