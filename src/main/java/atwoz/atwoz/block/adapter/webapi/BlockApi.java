package atwoz.atwoz.block.adapter.webapi;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.block.adapter.webapi.dto.BlockCreateRequest;
import atwoz.atwoz.block.application.provided.BlockCommander;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "차단 API")
@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockApi {
    private final BlockCommander blockCommander;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createBlock(
        @RequestBody @Valid BlockCreateRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        Long blockerId = authContext.getId();
        blockCommander.createBlock(blockerId, request.blockedId());
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
