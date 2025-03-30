package org.hisudoku.hisudokuapi.sudokus.models;

import lombok.Data;

import java.util.List;

@Data
public class SudokuFeedModel {
    private final List<SudokuModel> sudokus;
    private final Boolean hasNextPage;
    private final String cursor;
}
