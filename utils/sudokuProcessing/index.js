/*
    Gather all functionalities related to data processing and re-export them
*/

module.exports = {
    extractColumnsFromTheBoard : require("./extractColumnsFromTheBoard"),
    isArraySolved : require("./isArraySolved"),
    isBoardValid : require("./isBoardValid"),
    reorganizeData : require("./reorganizeData"),
    sudokuProcessing : require("./sudokuProcessing"),
    validateArray : require("./validateArray"),
    isSudokuValid : require("./isSudokuValid")
}