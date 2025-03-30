package org.hisudoku.hisudokuapi.general.exceptions;

import lombok.Getter;

public class UserNotFoundException extends RuntimeException {
    public enum ByProperty {
        NAME, ID, EMAIL
    }

    @Getter
    private final ByProperty byProperty;

    @Getter
    private final String determinant;

    public UserNotFoundException(ByProperty byProperty, String determinant) {
        super(determinant); // Always place the super() call as the first statement in the subclass constructor
        this.byProperty = byProperty;
        this.determinant = determinant;
    }
}