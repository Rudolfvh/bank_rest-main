package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.*;
import com.example.bankcards.entity.User;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(com.example.bankcards.entity.Role.valueOf(request.getRole()))")
    User toEntity(CreateUserRequest request);
}