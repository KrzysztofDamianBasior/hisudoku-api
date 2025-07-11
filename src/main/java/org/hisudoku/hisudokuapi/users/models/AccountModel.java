package org.hisudoku.hisudokuapi.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuFeedModel;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountModel {
    private String id;
    private SudokuFeedModel createdSudokus;
    private LocalDateTime enrollmentDate;
    private LocalDateTime updatedAt;
    private String name;
    private String email;
    // private final String inactiveEmailAddress;
    private String role;
}
