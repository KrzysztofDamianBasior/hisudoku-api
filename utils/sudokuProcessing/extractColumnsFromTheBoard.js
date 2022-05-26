module.exports = (board, columnNum) => {
    return board.reduce((total, row) => [...total, row[columnNum]], [])
}