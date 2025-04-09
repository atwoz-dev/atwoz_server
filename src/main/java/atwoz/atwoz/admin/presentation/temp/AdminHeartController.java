package atwoz.atwoz.admin.presentation.temp;

import atwoz.atwoz.admin.command.application.temp.AdminTempService;
import atwoz.atwoz.admin.presentation.temp.dto.GrantMissionHeartRequest;
import atwoz.atwoz.common.enums.StatusType;
import atwoz.atwoz.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminHeartController {

    private final AdminTempService adminTempService;

    @PatchMapping("/mission-heart")
    public ResponseEntity<BaseResponse<Void>> grantMissionHeart(@Valid @RequestBody GrantMissionHeartRequest request) {
        adminTempService.grantMissionHeart(request);
        return ResponseEntity.ok(BaseResponse.from(StatusType.OK));
    }
}
