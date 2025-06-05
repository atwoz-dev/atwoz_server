package atwoz.atwoz.match.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.match.command.application.match.MatchService;
import atwoz.atwoz.match.presentation.dto.MatchRequestDto;
import atwoz.atwoz.match.presentation.dto.MatchResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "메시지(매칭) 관리 API")
@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @Operation(summary = "메시지 전송 (매칭 요청)")
    @PostMapping("/request")
    public ResponseEntity<BaseResponse<Void>> requestMatch(@RequestBody MatchRequestDto matchRequestDto,
        @AuthPrincipal AuthContext authContext) {
        matchService.request(authContext.getId(), matchRequestDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "메시지 응답 (매칭 수락)")
    @PatchMapping("/{matchId}/approve")
    public ResponseEntity<BaseResponse<Void>> approveMatch(@PathVariable Long matchId,
        @RequestBody MatchResponseDto matchResponseDto, @AuthPrincipal AuthContext authContext) {
        matchService.approve(matchId, authContext.getId(), matchResponseDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "메시지 거절 (매칭 거절)")
    @PatchMapping("/{matchId}/reject")
    public ResponseEntity<BaseResponse<Void>> rejectMatch(@PathVariable Long matchId,
        @AuthPrincipal AuthContext authContext) {
        matchService.reject(matchId, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @Operation(summary = "메시지 읽음 처리 (매칭 읽음 처리)")
    @PatchMapping("/{matchId}/check")
    public ResponseEntity<BaseResponse<Void>> check(@PathVariable Long matchId,
        @AuthPrincipal AuthContext authContext) {
        matchService.rejectCheck(authContext.getId(), matchId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
