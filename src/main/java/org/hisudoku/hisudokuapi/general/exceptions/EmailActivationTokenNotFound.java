package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class EmailActivationTokenNotFound extends RuntimeException {
    @Getter
    private final String token;

    public EmailActivationTokenNotFound(String token) {
        super(token);
        this.token = token;
    }
}
