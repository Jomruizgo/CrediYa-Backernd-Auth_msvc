package com.crediya.api.dto.request;

import com.crediya.util.Constant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = Constant.OPENAPI_REFRESH_TOKEN_REQUEST_DESC)
public record RefreshTokenRequestDto(
    @Schema(description = Constant.OPENAPI_REFRESH_TOKEN_DESC)
    @NotBlank(message = Constant.DTO_REFRESH_TOKEN_REQUIRED)
    String refreshToken
) {
}