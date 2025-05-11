package io.github.enessaidtatli.mapper;

import io.github.enessaidtatli.dto.request.RegisterDto;
import io.github.enessaidtatli.dto.response.RegisterResponseDto;
import io.github.enessaidtatli.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDataMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(RegisterDto registerDto){
        return User.builder()
                .id(UUID.randomUUID())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .phoneNumber(registerDto.getPhoneNumber())
                .build();
    }

    public RegisterResponseDto toResponse(User entity){
        return RegisterResponseDto.builder()
                .userId(entity.getId().toString())
                .build();
    }
}
