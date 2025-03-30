package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class SudokuNotFoundException extends RuntimeException {
    @Getter
    private final String sudokuId;

    public SudokuNotFoundException(String sudokuId) {
        super(sudokuId);
        this.sudokuId = sudokuId;
    }
}