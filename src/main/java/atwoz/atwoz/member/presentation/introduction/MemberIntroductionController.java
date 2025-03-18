package atwoz.atwoz.member.presentation.introduction;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.member.query.introduction.application.IntroductionQueryService;
import atwoz.atwoz.member.query.introduction.application.MemberIntroductionProfileView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/introduction")
public class MemberIntroductionController {
    private final IntroductionQueryService introductionQueryService;

    @GetMapping("/grade")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findDiamondGradeIntroductions(@AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findDiamondGradeIntroductions(memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/hobby")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameHobbyIntroductions(@AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameHobbyIntroductions(memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/religion")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameReligionIntroductions(@AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameReligionIntroductions(memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/region")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findSameRegionIntroductions(@AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findSameRegionIntroductions(memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }

    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<List<MemberIntroductionProfileView>>> findRecentlyJoinedIntroductions(@AuthPrincipal AuthContext authContext) {
        long memberId = authContext.getId();
        List<MemberIntroductionProfileView> introductionProfileViews = introductionQueryService.findRecentlyJoinedIntroductions(memberId);
        return ResponseEntity.ok(BaseResponse.of(StatusType.OK, introductionProfileViews));
    }
}
