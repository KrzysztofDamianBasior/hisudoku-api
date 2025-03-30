package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class EmailTakenException extends RuntimeException {
    @Getter
    private final String email;

    public EmailTakenException(String email) {
        super(email);
        this.email = email;
    }
}