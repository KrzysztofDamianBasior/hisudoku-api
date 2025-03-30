package org.hisudoku.hisudokuapi.users.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrantAdminPermissionsInput {
    @NotBlank
    private String userId;
}
