package io.github.enessaidtatli.handler;

import io.github.enessaidtatli.dto.request.RegisterDto;
import io.github.enessaidtatli.config.usecase.NoUseCaseHandler;
import io.github.enessaidtatli.config.usecase.ObservablePublisher;
import io.github.enessaidtatli.dto.response.RegisterResponseDto;
import io.github.enessaidtatli.exception.EmailDuplicationException;
import io.github.enessaidtatli.mapper.UserDataMapper;
import io.github.enessaidtatli.model.User;
import io.github.enessaidtatli.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Component
@Validated
public class Register extends ObservablePublisher implements NoUseCaseHandler<RegisterResponseDto, RegisterDto> {

    private final UserRepository userRepository;
    private final UserDataMapper userDataMapper;

    public Register(UserRepository userRepository, UserDataMapper userDataMapper){
        this.userRepository = userRepository;
        this.userDataMapper = userDataMapper;
        register(RegisterDto.class, this);
    }

    @Override
    public RegisterResponseDto handle(@Valid RegisterDto dto) {
        checkMailDuplication(dto.getEmail());
        return userDataMapper.toResponse(userRepository.save(userDataMapper.toEntity(dto)));
    }

    private void checkMailDuplication(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            log.error("This email already in use, try again !");
            throw new EmailDuplicationException("This email already in use, try again !");
        }
    }
}
