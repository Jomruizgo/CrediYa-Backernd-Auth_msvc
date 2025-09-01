package com.crediya.api.mapper;

import com.crediya.api.dto.response.AuthenticationResponseDto;
import com.crediya.model.AuthenticationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public interface AuthenticationResponseMapper {
    
    @Mapping(source = "accessToken.value", target = "accessToken")
    @Mapping(source = "refreshToken.value", target = "refreshToken")
    @Mapping(source = "user", target = "user")
    AuthenticationResponseDto toDto(AuthenticationResponse authenticationResponse);
}