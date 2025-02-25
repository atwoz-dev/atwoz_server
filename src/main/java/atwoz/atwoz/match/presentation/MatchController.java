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

    @PutMapping("/approve")
    public ResponseEntity<BaseResponse<Void>> approveMatch(@RequestBody MatchResponseDto matchResponseDto, @AuthPrincipal AuthContext authContext) {
        matchService.approve(authContext.getId(), matchResponseDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PutMapping("/reject")
    public ResponseEntity<BaseResponse<Void>> rejectMatch(@RequestBody MatchResponseDto matchResponseDto, @AuthPrincipal AuthContext authContext) {
        matchService.approve(authContext.getId(), matchResponseDto);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }

    @PutMapping("/check/{id}")
    public ResponseEntity<BaseResponse<Void>> check(@AuthPrincipal AuthContext authContext, @PathVariable Long id) {
        matchService.rejectCheck(authContext.getId(), id);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
