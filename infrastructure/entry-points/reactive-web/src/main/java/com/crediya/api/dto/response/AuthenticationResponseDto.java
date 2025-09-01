package com.crediya.api.dto.response;

import com.crediya.api.dto.response.UserResponseDto;
import com.crediya.util.Constant;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = Constant.OPENAPI_AUTH_RESPONSE_DESC)
public record AuthenticationResponseDto(
    @Schema(description = Constant.OPENAPI_ACCESS_TOKEN_DESC)
    String accessToken,
    @Schema(description = Constant.OPENAPI_REFRESH_TOKEN_RESPONSE_DESC)
    String refreshToken,
    @Schema(description = Constant.OPENAPI_USER_INFO_DESC)
    UserResponseDto user
) {
}