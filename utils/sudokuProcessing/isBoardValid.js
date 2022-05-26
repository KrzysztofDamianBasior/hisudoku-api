module.exports = (board) => {

    const rows = [[],[],[],[],[],[],[],[],[]]
    const columns = [[],[],[],[],[],[],[],[],[]]
    const boxes = [[],[],[],[],[],[],[],[],[]]
  
    board.forEach( (boardRows, rowIndex) => {
      rows.forEach( (num, colIndex) => {
        if(num !== 0) {
          if(rows[rowIndex].includes(num)){
            return false
          } else rows[rowIndex].push(num)
          if(columns[colIndex].includes(num)){
            return false
          } else columns[colIndex].push(num)
          let boxIndex = Math.floor(rowIndex / 3) * 3 + Math.floor(colIndex / 3);
          if(boxes[boxIndex].includes(num)){
            return false
          } else boxes[boxIndex].push(num)
        }
      })
    })
    return true
  }