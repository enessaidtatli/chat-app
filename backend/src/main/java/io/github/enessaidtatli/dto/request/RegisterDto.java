package io.github.enessaidtatli.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class RegisterDto {

    @NotNull
    @NotBlank
    @Size.List({
            @Size(min = 3, message = ""),
            @Size(max = 15, message = "")
    })
    private final String userName;

    @NotNull
    @NotBlank
    @Size.List({
            @Size(min = 3, message = ""),
            @Size(max = 15, message = "")
    })
    private final String firstName;

    @NotNull
    @NotBlank
    @Size.List({
            @Size(min = 3, message = ""),
            @Size(max = 15, message = "")
    })
    private final String lastName;

    @NotNull
    @Pattern(regexp = "^\\+90\\s\\d{3}\\s\\d{3}\\s\\d{2}\\s\\d{2}$")
    private final String phoneNumber;

    @NotNull
    @NotBlank
    @Email
    @Size.List({
            @Size(min = 3, message = ""),
            @Size(max = 20, message = "")
    })
    private final String email;

    @NotNull
    @NotBlank
    @Size.List({
            @Size(min = 8, message = ""),
            @Size(max = 30, message = "")
    })
    @Pattern(regexp = "^(?=.{8,30}$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).*$",
    message = "Password must be 8–30 chars with at least one uppercase, lowercase, digit and special character")
    private final String password;

}
