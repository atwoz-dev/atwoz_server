package atwoz.atwoz.match.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.match.command.application.match.MatchService;
import atwoz.atwoz.match.presentation.dto.MatchRequestDto;
import atwoz.atwoz.match.presentation.dto.MatchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/request")
    public ResponseEntity<BaseResponse<Void>> requestMatch(@RequestBody MatchRequestDto matchRequestDto, @AuthPrincipal AuthContext authContext) {
        matchService.request(authContext.getId(), matchRequestDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{matchId}/approve")
    public ResponseEntity<BaseResponse<Void>> approveMatch(@PathVariable Long matchId, @RequestBody MatchResponseDto matchResponseDto, @AuthPrincipal AuthContext authContext) {
        matchService.approve(matchId, authContext.getId(), matchResponseDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{matchId}/reject")
    public ResponseEntity<BaseResponse<Void>> rejectMatch(@PathVariable Long matchId, @RequestBody MatchResponseDto matchResponseDto, @AuthPrincipal AuthContext authContext) {
        matchService.reject(matchId, authContext.getId(), matchResponseDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PatchMapping("/{matchId}/check")
    public ResponseEntity<BaseResponse<Void>> check(@PathVariable Long matchId, @AuthPrincipal AuthContext authContext) {
        matchService.rejectCheck(authContext.getId(), matchId);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
