package atwoz.atwoz.like.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.like.command.application.LikeSendService;
import atwoz.atwoz.like.query.LikeQueryService;
import atwoz.atwoz.like.query.LikeView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeSendService likeSendService;
    private final LikeQueryService likeQueryService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> send(
        @Valid @RequestBody LikeSendRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final long senderId = authContext.getId();
        likeSendService.send(senderId, request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @GetMapping("/sent")
    public ResponseEntity<BaseResponse<List<LikeView>>> getSentLikes(
        @ModelAttribute LikeListRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final var sentLikes = likeQueryService.findSentLikes(authContext.getId(), request.lastLikeId());
        return ResponseEntity.ok(BaseResponse.of(OK, sentLikes));
    }

    @GetMapping("/received")
    public ResponseEntity<BaseResponse<List<LikeView>>> getReceivedLikes(
        @ModelAttribute LikeListRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final var receivedLikes = likeQueryService.findReceivedLikes(authContext.getId(), request.lastLikeId());
        return ResponseEntity.ok(BaseResponse.of(OK, receivedLikes));
    }
}
