package atwoz.atwoz.admin.presentation.member;

import atwoz.atwoz.admin.query.member.AdminMemberQueryRepository;
import atwoz.atwoz.admin.query.member.MemberDetailView;
import atwoz.atwoz.admin.query.member.MemberSearchCondition;
import atwoz.atwoz.admin.query.member.MemberView;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "관리자 페이지 멤버 관리 API")
@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberQueryRepository adminMemberQueryRepository;

    @Operation(summary = "멤버 목록 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<MemberView>>> getMembers(
        @Valid @ModelAttribute MemberSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.of(OK, adminMemberQueryRepository.findMembers(condition, pageable)));
    }

    @Operation(summary = "멤버 상세 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse<MemberDetailView>> getMemberDetail(@PathVariable long memberId) {
        return ResponseEntity.ok(BaseResponse.of(OK, adminMemberQueryRepository.findById(memberId)));
    }
}
