package atwoz.atwoz.common.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseResponse<T>(
        int status,
        String code,
        String message,
        T data
) {
    public static <T> BaseResponse<T> from(StatusType statusType) {
        return new BaseResponse<>(statusType);
    }

    public static <T> BaseResponse<T> of(StatusType statusType, T data) {
        return new BaseResponse<>(statusType, data);
    }

    public BaseResponse(StatusType statusType) {
        this(statusType.getStatus(), statusType.getCode(), statusType.getMessage(), null);
    }

    public BaseResponse(StatusType statusType, T data) {
        this(statusType.getStatus(), statusType.getCode(), statusType.getMessage(), data);
    }
}
