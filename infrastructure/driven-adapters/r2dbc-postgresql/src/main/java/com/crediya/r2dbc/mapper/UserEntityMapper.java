package com.crediya.r2dbc.mapper;

import com.crediya.model.User;
import com.crediya.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);
}