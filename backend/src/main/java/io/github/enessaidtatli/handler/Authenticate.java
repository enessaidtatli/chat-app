package io.github.enessaidtatli.handler;

import io.github.enessaidtatli.dto.request.LogInDto;
import io.github.enessaidtatli.config.usecase.NoUseCaseHandler;
import io.github.enessaidtatli.config.usecase.ObservablePublisher;
import io.github.enessaidtatli.dto.response.AuthenticateResponseDto;
import io.github.enessaidtatli.exception.SourceNotFoundException;
import io.github.enessaidtatli.model.User;
import io.github.enessaidtatli.repository.UserRepository;
import io.github.enessaidtatli.security.CustomUserDetailsService;
import io.github.enessaidtatli.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Component
@Validated
public class Authenticate extends ObservablePublisher implements NoUseCaseHandler<AuthenticateResponseDto, LogInDto> {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public Authenticate(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService){
        register(LogInDto.class, this);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticateResponseDto handle(@Valid LogInDto dto) {
        final String email = dto.getEmail();
        Optional<User> optUser = userRepository.findByEmail(email);
        if(optUser.isPresent()){
            User user = optUser.get();
            checkPasswords(dto.getPassword(), user.getPassword());
            String token = authenticateUser(dto);
            log.info("Generated token from authentication = {}", token);
        } else {
            log.error("User with email = {} could not be found!", email);
            throw new SourceNotFoundException("User with email " + email + " could not be found!");
        }
        return new AuthenticateResponseDto("User with email = " + email + "authenticated successfully !");
    }

    private void checkPasswords(String rawPassword, String hashedPassword) {
        if(!passwordEncoder.matches(rawPassword, hashedPassword)){
            log.error("Provided password or email is not correct !");
            throw new RuntimeException();
        }
    }

    private String authenticateUser(LogInDto dto){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        return jwtUtil.generateToken(userDetails);
    }

}
