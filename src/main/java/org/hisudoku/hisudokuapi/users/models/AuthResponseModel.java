package org.hisudoku.hisudokuapi.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseModel {
    private String accessToken;
}
