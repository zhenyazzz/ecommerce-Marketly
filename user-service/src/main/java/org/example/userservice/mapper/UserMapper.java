package org.example.userservice.mapper;

import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.ProfileResponse;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.kafka.event.UserProfileUpdatedEvent;
import org.example.userservice.kafka.event.UserRegistrationEvent;
import org.example.userservice.model.User;
import org.mapstruct.*;

import java.time.Instant;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Instant.class, Collectors.class})
public interface UserMapper {

    UserResponse toUserResponse(User user);

    ProfileResponse toProfileResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "roles", source = "roles")
    User toUser(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))")
    UserRegistrationEvent toUserRegistrationEvent(User user);

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    UserProfileUpdatedEvent toUserProfileUpdatedEvent(User user);
} 