package com.crediya.api.mapper;

import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {
    User toDomain(CreateUserRequestDto createUserRequestDto);
    User toDomain(UpdateUserRequestDto updateUserRequestDto);
}