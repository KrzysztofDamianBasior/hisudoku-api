package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class NameTakenException extends RuntimeException {
    @Getter
    private final String username;

    public NameTakenException(String username) {
        super(username);
        this.username = username;
    }
}