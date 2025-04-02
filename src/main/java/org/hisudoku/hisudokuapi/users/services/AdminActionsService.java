package org.hisudoku.hisudokuapi.users.services;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.exceptions.NameTakenException;
import org.hisudoku.hisudokuapi.general.exceptions.OperationFailedException;
import org.hisudoku.hisudokuapi.general.exceptions.UserNotFoundException;
import org.hisudoku.hisudokuapi.users.dtos.GrantAdminPermissionsInput;
import org.hisudoku.hisudokuapi.users.dtos.RemoveOneInput;
import org.hisudoku.hisudokuapi.users.dtos.UpdateOneUsernameInput;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.enums.Role;
import org.hisudoku.hisudokuapi.users.models.MessageResponseModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminActionsService {
    private final HSUserComplexQueriesRepository hsUserComplexQueriesRepository;

    public UserModel grantOneAdminPermissions(GrantAdminPermissionsInput grantAdminPermissionsInput) {
        String idOfTheUserToWhomAdministratorPrivilegesShouldBeGranted = grantAdminPermissionsInput.getUserId();
        return hsUserComplexQueriesRepository.findOneByIdUpdateRole(idOfTheUserToWhomAdministratorPrivilegesShouldBeGranted, Role.ADMIN.name())
                .map(HSUserUtils::mapToUserModelDTO)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.ID, idOfTheUserToWhomAdministratorPrivilegesShouldBeGranted));
    }

    public UserModel banOne(String idOfUserToBeBanned) {
        return hsUserComplexQueriesRepository.findOneByIdUpdateRole(idOfUserToBeBanned, Role.BANNED.name())
                .map(HSUserUtils::mapToUserModelDTO)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.ID, idOfUserToBeBanned));
    }

    public UserModel updateOneUsername(UpdateOneUsernameInput updateOneUsernameInput) {
        String userId=updateOneUsernameInput.getUserId();
        String newUsername=updateOneUsernameInput.getNewUsername();

        if(this.hsUserComplexQueriesRepository.doesNameExist(newUsername)){
            throw new NameTakenException(newUsername);
        }

        return this.hsUserComplexQueriesRepository.updateOneUsername(userId, newUsername)
                .map(HSUserUtils::mapToUserModelDTO)
                .orElseThrow(()->new OperationFailedException("update username"));
    }

    public MessageResponseModel removeOne(RemoveOneInput removeOneInput) {
        String id = removeOneInput.getUserId();
        HSUser user = this.hsUserComplexQueriesRepository.removeOneById(id)
                .orElseThrow(() -> new OperationFailedException("remove one"));
        return new MessageResponseModel("removed user with id: " + user.getId());
    }
}
