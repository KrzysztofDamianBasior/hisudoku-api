module.exports = (sudokuString) => {
    const rows = sudokuString.split(";")
    let board = []
    for (const key in rows) {
      let matches = rows[key].match(/[0-9]/g)
        
      if(matches !== null){
        matches = matches.map( (num) => parseInt(num) )
        board.push(matches)
      }
    }
    return board
}