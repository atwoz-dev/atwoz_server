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

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "member_ideals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberIdeal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public static MemberIdeal of(
            Long memberId,
            AgeRange ageRange,
            Set<Long> hobbyIds,
            Region region,
            Religion religion,
            SmokingStatus smokingStatus,
            DrinkingStatus drinkingStatus
    ) {
        return new MemberIdeal(memberId, ageRange, hobbyIds, region, religion, smokingStatus, drinkingStatus);
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
        this.memberId = memberId;
        this.ageRange = ageRange;
        this.hobbyIds = hobbyIds;
        this.region = region;
        this.religion = religion;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
    }
}
