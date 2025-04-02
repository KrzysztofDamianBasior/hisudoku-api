package org.hisudoku.hisudokuapi.users.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.hisudoku.hisudokuapi.general.validators.ConfigurableSize;

@Data
@AllArgsConstructor
public class UpdateMyPasswordInput {
    @NotBlank
    @ConfigurableSize(
            maxProperty = "application.validation.max-password-length",
            minProperty = "application.validation.min-password-length",
            fieldName = "newPassword"
    )
    private String newPassword;
}
