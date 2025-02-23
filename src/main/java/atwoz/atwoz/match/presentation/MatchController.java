package atwoz.atwoz.match.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.match.command.application.match.MatchService;
import atwoz.atwoz.match.presentation.dto.MatchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/request/{id}")
    public ResponseEntity<BaseResponse<Void>> requestMatch(@PathVariable("id") long responderId, @RequestBody MatchRequestDto matchRequestDto, @AuthPrincipal AuthContext authContext) {
        matchService.request(authContext.getId(), responderId, matchRequestDto.requestMessage());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
