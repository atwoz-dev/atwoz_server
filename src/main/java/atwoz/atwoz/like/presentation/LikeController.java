package atwoz.atwoz.like.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.like.command.application.LikeSendService;
import atwoz.atwoz.like.query.LikeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "좋아요 관리 API")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeSendService likeSendService;
    private final LikeQueryService likeQueryService;

    @Operation(summary = "좋아요 보내기")
    @PostMapping
    public ResponseEntity<BaseResponse<LikeSendResponse>> send(
        @Valid @RequestBody LikeSendRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final long senderId = authContext.getId();
        boolean hasProcessedMission = likeSendService.send(senderId, request);
        LikeSendResponse response = new LikeSendResponse(hasProcessedMission);
        return ResponseEntity.ok(BaseResponse.of(OK, response));
    }

    @Operation(summary = "내가 보낸 좋아요 목록 조회")
    @GetMapping("/sent")
    public ResponseEntity<BaseResponse<LikeViews>> getSentLikes(
        @ModelAttribute LikeListRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final var sentLikes = likeQueryService.findSentLikes(authContext.getId(), request.lastLikeId());
        return ResponseEntity.ok(BaseResponse.of(OK, sentLikes));
    }

    @Operation(summary = "내가 받은 좋아요 목록 조회")
    @GetMapping("/received")
    public ResponseEntity<BaseResponse<LikeViews>> getReceivedLikes(
        @ModelAttribute LikeListRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        final var receivedLikes = likeQueryService.findReceivedLikes(authContext.getId(), request.lastLikeId());
        return ResponseEntity.ok(BaseResponse.of(OK, receivedLikes));
    }
}
