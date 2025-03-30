package org.hisudoku.hisudokuapi.sudokus.models;

import lombok.Data;

import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;

import java.time.LocalDateTime;

@Data
public class SudokuModel {
    private final String id;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UserModel author;
    private final String content;
    private final Integer favouriteCount;
    private final UserFeedModel favouritedBy;
}

