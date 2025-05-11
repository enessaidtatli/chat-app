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
    private final String email;
    @NotNull
    @NotBlank
    @Size.List({
            @Size(min = 8, message = ""),
            @Size(max = 30, message = "")
    })
    @Pattern(regexp = "")
    private final String password;

}
