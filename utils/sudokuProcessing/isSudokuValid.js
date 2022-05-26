/*
 Check each row for duplicates.
 Check each column for duplicates.
 Check each 3x3 sub grid for duplicates.
 Return false if any duplicates are found, and true if no duplicates are found.
*/

const reorganizeData = require("./reorganizeData")
const isBoardValid = require("./isBoardValid")

module.exports = (sudokuString) => {
  const board = reorganizeData(sudokuString)
  return isBoardValid(board)
}
