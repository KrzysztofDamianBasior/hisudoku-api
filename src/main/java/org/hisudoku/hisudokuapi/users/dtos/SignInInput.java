package org.hisudoku.hisudokuapi.users.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.hisudoku.hisudokuapi.general.validators.ConfigurableSize;

@Data
@AllArgsConstructor
public class SignInInput {
    @NotBlank(message = "{sign-up-input.username.not-blank}")
    @ConfigurableSize(
            maxProperty = "application.validation.max-username-length",
            minProperty = "application.validation.min-username-length"
    )
    private String username;

    @NotBlank(message = "{sign-up-input.password.not-blank}")
    @ConfigurableSize(
            maxProperty = "application.validation.max-password-length",
            minProperty = "application.validation.min-password-length"
    )
    private String password;
}
