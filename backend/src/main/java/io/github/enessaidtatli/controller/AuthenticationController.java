package io.github.enessaidtatli.controller;

import io.github.enessaidtatli.controller.restresponse.Response;
import io.github.enessaidtatli.dto.request.LogInDto;
import io.github.enessaidtatli.dto.request.RegisterDto;
import io.github.enessaidtatli.dto.response.AuthenticateResponseDto;
import io.github.enessaidtatli.dto.response.RegisterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController extends BaseController {

    @PostMapping("/login")
    public Response<AuthenticateResponseDto> authenticate(@RequestBody LogInDto dto){
        AuthenticateResponseDto response = publish(dto);
        return respond(response, "Authenticated successfully !");
    }

    @PostMapping("/register")
    public Response<RegisterResponseDto> register(@RequestBody RegisterDto dto){
        RegisterResponseDto response = publish(dto);
        return respond(response, "Registered successfully");
    }

}
