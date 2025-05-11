package io.github.enessaidtatli.controller;

import io.github.enessaidtatli.config.usecase.BeanAwareHandlerPublisher;
import io.github.enessaidtatli.dto.request.LogInDto;
import io.github.enessaidtatli.dto.request.RegisterDto;
import io.github.enessaidtatli.dto.response.RegisterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController extends BeanAwareHandlerPublisher {

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LogInDto dto){
        return ResponseEntity.ok(publish(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> authenticate(@RequestBody RegisterDto dto){
        return ResponseEntity.ok(publish(dto));
    }

}
