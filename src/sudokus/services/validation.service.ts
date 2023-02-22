import { Injectable } from '@nestjs/common';

@Injectable()
export class ValidationService {
  extractColumnFromTheBoard({
    board,
    columnNum,
  }: {
    board: number[][];
    columnNum: number;
  }) {
    return board.reduce((total, row) => [...total, row[columnNum]], []);
  }

  isFragmentSolved(array: number[]) {
    const fragment = array.slice(0).sort().join(''),
      passingFragment = [1, 2, 3, 4, 5, 6, 7, 8, 9].join('');
    return fragment === passingFragment;
  }

  isBoardValid(board: number[][]) {
    const rows = [[], [], [], [], [], [], [], [], []];
    const columns = [[], [], [], [], [], [], [], [], []];
    const boxes = [[], [], [], [], [], [], [], [], []];

    board.forEach((boardRow, rowIndex) => {
      boardRow.forEach((num, colIndex) => {
        if (num !== 0) {
          if (rows[rowIndex].includes(num)) {
            return false;
          } else rows[rowIndex].push(num);
          if (columns[colIndex].includes(num)) {
            return false;
          } else columns[colIndex].push(num);

          const boxIndex =
            Math.floor(rowIndex / 3) * 3 + Math.floor(colIndex / 3);
          if (boxes[boxIndex].includes(num)) {
            return false;
          } else boxes[boxIndex].push(num);
        }
      });
    });
    return true;
  }

  stringToBoard(sudokuString: string) {
    const rows = sudokuString.split(';');
    const board: number[][] = [];

    for (const key in rows) {
      const matches = rows[key].match(/[0-9]/g);

      if (matches !== null) {
        const numbers = matches.map((num) => parseInt(num));
        board.push(numbers);
      }
    }
    return board;
  }

  validateSudokuString(str: string) {
    return /^([0-9]{3}\.[0-9]{3}\.[0-9]{3};){9}$/.test(str);
  }

  isFragmentValid({
    array,
    emptyCharacter = '0',
  }: {
    array: string[];
    emptyCharacter: string;
  }) {
    const digits = array.filter((character) => character !== emptyCharacter);
    return digits.length === [...new Set(digits)].length;
  }

  isSudokuStringValidBoard(sudokuString: string) {
    /*
        Check each row for duplicates.
        Check each column for duplicates.
        Check each 3x3 sub grid for duplicates.
        Return false if any duplicates are found, and true if no duplicates are found.
    */
    //reorganize data
    const board = this.stringToBoard(sudokuString);
    return this.isBoardValid(board);
  }
}
