package io.github.enessaidtatli.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LogInDto {

    @NotNull
    @NotBlank
    @Email
    @Size.List({
            @Size(min = 3, message = ""),
            @Size(max = 20, message = "")
    })
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private final String email;
    @NotNull
    @NotBlank
    @Size.List({
            @Size(min = 8, message = ""),
            @Size(max = 30, message = "")
    })
    @Pattern(regexp = "^(?=.{8,30}$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).*$")
    private final String password;

}
