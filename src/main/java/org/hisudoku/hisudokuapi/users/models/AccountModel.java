package org.hisudoku.hisudokuapi.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AccountModel {
    private String id;
    private List<String> createdSudokus;
    private LocalDateTime enrollmentDate;
    private LocalDateTime updatedAt;
    private String name;
    private String email;
    // private final String inactiveEmailAddress;
    private String role;
}
