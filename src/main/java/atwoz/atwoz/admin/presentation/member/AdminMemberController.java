package atwoz.atwoz.admin.presentation.member;

import atwoz.atwoz.admin.query.member.AdminMemberQueryRepository;
import atwoz.atwoz.admin.query.member.MemberDetailView;
import atwoz.atwoz.admin.query.member.MemberSearchCondition;
import atwoz.atwoz.admin.query.member.MemberView;
import atwoz.atwoz.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberQueryRepository adminMemberQueryRepository;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<MemberView>>> getMembers(
        @Valid @ModelAttribute MemberSearchCondition condition,
        @PageableDefault(size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.of(OK, adminMemberQueryRepository.findMembers(condition, pageable)));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse<MemberDetailView>> getMemberDetail(@PathVariable long memberId) {
        return ResponseEntity.ok(BaseResponse.of(OK, adminMemberQueryRepository.findById(memberId)));
    }
}
