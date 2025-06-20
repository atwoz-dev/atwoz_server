package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.MemberIntroductionService;
import atwoz.atwoz.member.command.application.introduction.TodayCardService;
import atwoz.atwoz.member.presentation.introduction.dto.MemberIntroductionCreateRequest;
import atwoz.atwoz.member.query.introduction.application.IntroductionQueryService;
import atwoz.atwoz.member.query.introduction.application.MemberIntroductionProfileView;
import atwoz.atwoz.member.query.introduction.application.TodayCardQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "소개받고 싶은 이성 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/introduction")
public class MemberIntroductionController {
    private final TodayCardService todayCardService;
    private final TodayCardQueryService todayCardQueryService;
    private final IntroductionQueryService introductionQueryService;
    private final MemberIntroductionService memberintroductionService;

    @Operation(summary = "다이아 등급 이성 조회")
    @GetMapping("/grade")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findDiamondGradeIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findDiamondGradeIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @Operation(summary = "취미가 같은 이성 조회")
    @GetMapping("/hobby")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameHobbyIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameHobbyIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @Operation(summary = "종교가 같은 이성 조회")
    @GetMapping("/religion")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameReligionIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameReligionIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @Operation(summary = "지역이 같은 이성 조회")
    @GetMapping("/city")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameCityIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameCityIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @Operation(summary = "최근에 가입한 이성 조회")
    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findRecentlyJoinedIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findRecentlyJoinedIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @Operation(summary = "오늘의 카드 생성 및 조회")
    @PostMapping("/today-card")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> createTodayCardIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        final Set<Long> todayCardMemberIds = todayCardQueryService.findTodayCardMemberIds(memberId);
        todayCardService.createTodayCardIntroductions(memberId, todayCardMemberIds);
        List<MemberIntroductionProfileView> introductionProfileViews = todayCardQueryService.findTodayCardIntroductions(
            memberId, todayCardMemberIds);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @Operation(summary = "다이아 등급 이성 프로필 블러 해제")
    @PostMapping("/grade")
    public ResponseEntity<BaseResponse<Void>> createGradeIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createGradeIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "취미가 같은 이성 프로필 블러 해제")
    @PostMapping("/hobby")
    public ResponseEntity<BaseResponse<Void>> createHobbyIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createHobbyIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "종교가 같은 이성 프로필 블러 해제")
    @PostMapping("/religion")
    public ResponseEntity<BaseResponse<Void>> createReligionIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createReligionIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "지역이 같은 이성 프로필 블러 해제")
    @PostMapping("/city")
    public ResponseEntity<BaseResponse<Void>> createCityIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createCityIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "최근에 가입한 이성 프로필 블러 해제")
    @PostMapping("/recent")
    public ResponseEntity<BaseResponse<Void>> createRecentIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createRecentIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
