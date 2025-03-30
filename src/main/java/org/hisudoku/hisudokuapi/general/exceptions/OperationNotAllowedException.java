package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class OperationNotAllowedException extends RuntimeException {
    @Getter
    private final String operation;

    public OperationNotAllowedException(String message) {
        super(message);
        this.operation = message;
    }
}
