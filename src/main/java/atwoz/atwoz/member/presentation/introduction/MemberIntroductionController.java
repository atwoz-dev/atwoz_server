package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.command.application.introduction.MemberIntroductionService;
import atwoz.atwoz.member.presentation.introduction.dto.MemberIntroductionCreateRequest;
import atwoz.atwoz.member.query.introduction.application.IntroductionQueryService;
import atwoz.atwoz.member.query.introduction.application.MemberIntroductionProfileView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/introduction")
public class MemberIntroductionController {
    private final IntroductionQueryService introductionQueryService;
    private final MemberIntroductionService memberintroductionService;

    @GetMapping("/grade")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findDiamondGradeIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findDiamondGradeIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/hobby")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameHobbyIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameHobbyIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/religion")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameReligionIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameReligionIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/city")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameCityIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameCityIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findRecentlyJoinedIntroductions(
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findRecentlyJoinedIntroductions(
            memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @PostMapping("/grade")
    public ResponseEntity<BaseResponse<Void>> createGradeIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createGradeIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PostMapping("/hobby")
    public ResponseEntity<BaseResponse<Void>> createHobbyIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createHobbyIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PostMapping("/religion")
    public ResponseEntity<BaseResponse<Void>> createReligionIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createReligionIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PostMapping("/city")
    public ResponseEntity<BaseResponse<Void>> createCityIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createCityIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PostMapping("/recent")
    public ResponseEntity<BaseResponse<Void>> createRecentIntroduction(
        @Valid @RequestBody MemberIntroductionCreateRequest request,
        @AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        memberintroductionService.createRecentIntroduction(memberId, request.introducedMemberId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
