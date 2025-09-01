package com.crediya.api.dto.request;

import com.crediya.util.Constant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = Constant.OPENAPI_LOGIN_REQUEST_DESC)
public record LoginRequestDto(
    @Schema(description = Constant.OPENAPI_EMAIL_DESC, example = Constant.OPENAPI_EMAIL_EXAMPLE)
    @NotBlank(message = Constant.DTO_EMAIL_REQUIRED)
    @Email(message = Constant.DTO_EMAIL_INVALID)
    String email,
    
    @Schema(description = Constant.OPENAPI_PASSWORD_DESC, example = Constant.OPENAPI_PASSWORD_EXAMPLE)
    @NotBlank(message = Constant.DTO_PASSWORD_REQUIRED)
    @Size(min = 8, message = Constant.DTO_PASSWORD_MIN_LENGTH)
    String password
) {
}