package org.hisudoku.hisudokuapi.sudokus.services;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.exceptions.OperationFailedException;
import org.hisudoku.hisudokuapi.general.exceptions.OperationNotAllowedException;
import org.hisudoku.hisudokuapi.general.exceptions.SudokuNotFoundException;
import org.hisudoku.hisudokuapi.general.exceptions.UserNotFoundException;
import org.hisudoku.hisudokuapi.sudokus.dtos.*;
import org.hisudoku.hisudokuapi.sudokus.entities.Sudoku;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuFeedModel;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuModel;
import org.hisudoku.hisudokuapi.sudokus.repositories.SudokuComplexQueriesRepository;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.enums.Role;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.models.MessageResponseModel;
import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;
import org.hisudoku.hisudokuapi.users.services.HSUserUtils;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SudokusService {
    private final SudokuComplexQueriesRepository sudokuComplexQueriesRepository;

    public SudokuFeedModel findManyByAuthor(String authorId, Integer limit, String sudokuCursor){
        if(sudokuCursor == null){
            return this.sudokuComplexQueriesRepository.findManyByAuthor(authorId, limit);
        } else {
            return this.sudokuComplexQueriesRepository.findManyByAuthor(authorId, limit, sudokuCursor);
        }
    }

    public SudokuModel addSudoku(HSUserPrincipal principal, AddSudokuInput addSudokuInput){
        Sudoku sudoku = this.sudokuComplexQueriesRepository.addOne(addSudokuInput.getContent(), principal.getId());

        return SudokuUtils.mapToSudokuModelFavouritedByNullAuthorNullDTO(sudoku);
    }

    public SudokuFeedModel sudokuFeed(Integer sudokusLimit, String sudokuCursor){
        if(sudokuCursor == null){
            return this.sudokuComplexQueriesRepository.findMany(sudokusLimit);
        } else  {
            return this.sudokuComplexQueriesRepository.findMany(sudokusLimit, sudokuCursor);
        }
    }

    public SudokuModel findOne(String sudokuId){
        Sudoku sudoku = this.sudokuComplexQueriesRepository.findOneById(sudokuId)
                .orElseThrow(()-> new SudokuNotFoundException(sudokuId));

        // repository.findById(id).map(Utils::mapToDTO).orElseThrow(() -> new NotFoundException(id));
        return SudokuUtils.mapToSudokuModelFavouritedByNullAuthorNullDTO(sudoku);
    }

    public SudokuModel updateSudokuContent(HSUserPrincipal principal, UpdateSudokuInput updateSudokuInput){
        this.verifySudokuAuthor(principal.getId(), principal.getRole(), updateSudokuInput.getSudokuId());

        Sudoku sudoku = this.sudokuComplexQueriesRepository.updateOneContent(updateSudokuInput.getSudokuId(), updateSudokuInput.getSudokuContent())
                .orElseThrow(()-> new OperationFailedException("sudoku: " + updateSudokuInput.getSudokuId()));

        return SudokuUtils.mapToSudokuModelFavouritedByNullAuthorNullDTO(sudoku);
    }

    public MessageResponseModel remove(HSUserPrincipal principal, RemoveSudokuInput removeSudokuInput){
        this.verifySudokuAuthor(principal.getId(), principal.getRole(), removeSudokuInput.getSudokuId());

        Sudoku sudoku = this.sudokuComplexQueriesRepository.removeOneById(removeSudokuInput.getSudokuId())
                .orElseThrow(()-> new OperationFailedException("remove one sudoku"));

        return new MessageResponseModel("removed sudoku with id: " + sudoku.getId());
    }

    public SudokuModel toggleFavorite(HSUserPrincipal principal, ToggleFavouriteSudokuInput toggleFavouriteSudokuInput){
        Sudoku sudoku = this.sudokuComplexQueriesRepository.toggleLike(toggleFavouriteSudokuInput.getSudokuId(), principal.getId())
                .orElseThrow(()-> new OperationFailedException("toggle favourite sudoku"));

        return SudokuUtils.mapToSudokuModelFavouritedByNullAuthorNullDTO(sudoku);
    }

    public UserModel findSudokuAuthor(String sudokuId) {
        HSUser author = this.sudokuComplexQueriesRepository.findSudokuAuthor(sudokuId)
                .orElseThrow(()-> new UserNotFoundException(UserNotFoundException.ByProperty.ID, sudokuId));

        return HSUserUtils.mapToUserModelDTO(author);
    }

    public Map<SudokuModel, UserModel> findSudokusAuthors(List<SudokuModel> sudokus) {
        return this.sudokuComplexQueriesRepository.findSudokusAuthors(sudokus);
    }

    public UserFeedModel findUsersWhoLikeSudoku(String sudokuId, int limit, String cursor) {
        if(cursor == null){
            return this.sudokuComplexQueriesRepository.findUsersWhoLikeSudoku(sudokuId, limit);
        } else {
            return this.sudokuComplexQueriesRepository.findUsersWhoLikeSudoku(sudokuId, limit, cursor);
        }
    }

    public void verifySudokuAuthor(String userId, Role userRole, String sudokuId) {
        HSUser author = this.sudokuComplexQueriesRepository.findSudokuAuthor(sudokuId)
                .orElseThrow(()-> new UserNotFoundException(UserNotFoundException.ByProperty.ID, sudokuId));

        boolean isOperationAllowed = author.getId().equals(userId) || userRole.equals(Role.ADMIN);

        if(!isOperationAllowed){
            throw new OperationNotAllowedException("lack of permissions");
        }
    }
}
