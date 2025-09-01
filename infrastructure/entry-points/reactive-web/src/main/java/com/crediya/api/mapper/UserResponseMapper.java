package com.crediya.api.mapper;

import com.crediya.api.dto.response.UserResponseDto;
import com.crediya.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    
    UserResponseDto toDto(User user);
}