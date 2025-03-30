package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class OperationFailedException extends RuntimeException {
    @Getter
    private final String reason;

    public OperationFailedException(String reason) {
        super(reason);
        this.reason = reason;
    }
}
