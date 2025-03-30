package org.hisudoku.hisudokuapi.users.dtos;

import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordInput {
    @Email
    private String email;
}
