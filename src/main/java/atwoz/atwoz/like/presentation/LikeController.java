package atwoz.atwoz.like.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.like.command.application.LikeSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeSendService likeSendService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> send(
        @Valid @RequestBody LikeSendRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final long senderId = authContext.getId();
        likeSendService.send(senderId, request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
