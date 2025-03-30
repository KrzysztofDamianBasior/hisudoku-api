package org.hisudoku.hisudokuapi.sudokus.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateSudokuInput {
    @Size(min = 24, message = "{validation.mongo-id.size.too-short}")
    @Size(max = 24, message = "{validation.mongo-id.size.too-long}")
    @NotBlank
    private String sudokuId;

    @NotBlank
    private String sudokuContent;
}
