package org.hisudoku.hisudokuapi.users.services;

import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.models.AccountModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HSUserUtils {
    public static List<UserModel> mapToUserModelDTOs(List<HSUser> users) {
        return users.stream()
                .map(HSUserUtils::mapToUserModelDTO)
                .collect(Collectors.toList());
    }

    public static UserModel mapToUserModelDTO(HSUser user) {
        return new UserModel(
                user.getId(),
                user.getAccountUsageInfo().getEnrollmentDate(),
                user.getAccountUsageInfo().getUpdatedAt(),
                user.getName(),
                user.getRole()
        );
    }

    public static List<AccountModel> mapToAccountModelCreatedSudokusNullDTOs(List<HSUser> users) {
        return users.stream()
                .map(HSUserUtils::mapToAccountModelCreatedSudokusNullDTO)
                .collect(Collectors.toList());
    }

    public static AccountModel mapToAccountModelCreatedSudokusNullDTO(HSUser user) {
        return new AccountModel(
                user.getId(),
                null,
                user.getAccountUsageInfo().getEnrollmentDate(),
                user.getAccountUsageInfo().getUpdatedAt(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
