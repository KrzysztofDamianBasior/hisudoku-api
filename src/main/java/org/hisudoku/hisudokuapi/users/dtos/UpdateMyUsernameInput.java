package org.hisudoku.hisudokuapi.users.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.hisudoku.hisudokuapi.general.validators.ConfigurableSize;

@Data
@AllArgsConstructor
public class UpdateMyUsernameInput {
    @NotBlank()
    @ConfigurableSize(
            maxProperty = "application.validation.max-username-length",
            minProperty = "application.validation.min-username-length"
    )
    private String newUsername;
}
