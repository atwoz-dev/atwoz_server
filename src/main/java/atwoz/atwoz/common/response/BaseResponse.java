package atwoz.atwoz.common.response;

import atwoz.atwoz.common.enums.StatusType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseResponse<T>(
    int status,
    String code,
    String message,
    T data
) {
    public BaseResponse(StatusType statusType) {
        this(statusType.getStatus(), statusType.getCode(), statusType.getMessage(), null);
    }

    public BaseResponse(StatusType statusType, String message) {
        this(statusType.getStatus(), statusType.getCode(), message, null);
    }

    public BaseResponse(StatusType statusType, T data) {
        this(statusType.getStatus(), statusType.getCode(), statusType.getMessage(), data);
    }

    public static <T> BaseResponse<T> from(StatusType statusType) {
        return new BaseResponse<>(statusType);
    }

    public static <T> BaseResponse<T> of(StatusType statusType, String message) {
        return new BaseResponse<>(statusType, message);
    }

    public static <T> BaseResponse<T> of(StatusType statusType, T data) {
        return new BaseResponse<>(statusType, data);
    }
}
