package io.github.enessaidtatli.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AuthenticateResponseDto {

    private String message;
}
