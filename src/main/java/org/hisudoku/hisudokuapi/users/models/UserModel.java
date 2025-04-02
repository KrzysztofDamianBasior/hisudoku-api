package org.hisudoku.hisudokuapi.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserModel {
    private String id;
    private LocalDateTime enrollmentDate;
    private LocalDateTime updatedAt;
    private String name;
    private String role;
}
