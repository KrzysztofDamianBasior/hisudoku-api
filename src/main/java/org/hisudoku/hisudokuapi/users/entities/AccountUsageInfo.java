package org.hisudoku.hisudokuapi.users.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUsageInfo {
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")

    @Field(name = "enrollment_at")
    private LocalDateTime enrollmentDate;

    @Field(name = "updated_at")
    private LocalDateTime updatedAt;

    @Field(name = "last_logged_in")
    private LocalDateTime lastLoggedIn;

    // MongoDB stores dates in UTC, and this cannot be changed. Thus, if we want our date fields to be specific to a time zone, we can store the time zone offset in a separate field and do the conversion ourselves. Let’s add that field as a String:
    // public String timeZoneOffset;
}
