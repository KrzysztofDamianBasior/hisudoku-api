package org.hisudoku.hisudokuapi.users.services;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.exceptions.UserNotFoundException;
import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PublicActionsService {
    private final HSUserComplexQueriesRepository hsUserComplexQueriesRepository;

    public UserModel findOneById(String id) {
        return hsUserComplexQueriesRepository.findOneById(id)
                .map(HSUserUtils::mapToUserModelDTO)
                .orElseThrow(()->new UserNotFoundException(UserNotFoundException.ByProperty.ID, id));

        // repository.findById(id).map(Utils::mapToDTO).orElseThrow(() -> new NotFoundException(id));
    }

    public UserModel findOneByName(String name) {
        return hsUserComplexQueriesRepository.findOneByName(name)
                .map(HSUserUtils::mapToUserModelDTO)
                .orElseThrow(()->new UserNotFoundException(UserNotFoundException.ByProperty.NAME, name));
    }

    public UserFeedModel userFeed(Integer usersLimit, String userCursor) {
        if(Objects.isNull(userCursor)) {
            return hsUserComplexQueriesRepository.findMany(usersLimit);
        } else {
            return hsUserComplexQueriesRepository.findMany(usersLimit, userCursor);
        }
    }

    //    public UserFeedModel findManyByTheirIds() {
        //    return this.usersRepository.userFeedByTheirIds( ids, cursor, limit )
    //    }
}
