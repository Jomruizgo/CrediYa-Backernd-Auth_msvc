package com.crediya.api.mapper;

import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.model.Role;
import com.crediya.model.User;
import com.crediya.model.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(createUserRequestDto.role() != null ? createUserRequestDto.role() : Role.CLIENT)")
    @Mapping(target = "status", expression = "java(createUserRequestDto.status() != null ? createUserRequestDto.status() : UserStatus.PENDING)")
    User toDomain(CreateUserRequestDto createUserRequestDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toDomain(UpdateUserRequestDto updateUserRequestDto);
}