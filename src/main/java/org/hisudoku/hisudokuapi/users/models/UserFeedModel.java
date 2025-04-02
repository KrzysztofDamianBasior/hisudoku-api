package org.hisudoku.hisudokuapi.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserFeedModel {
    private List<UserModel> users;
    private Boolean hasNextPage;
    private String cursor;
}
