package org.hisudoku.hisudokuapi.sudokus.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddSudokuInput {
    @NotBlank
    private String content;
}
