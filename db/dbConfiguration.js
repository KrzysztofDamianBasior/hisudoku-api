const mongoose = require('mongoose');
const userSchema = require('./models/user')
const sudokuSchema = require('./models/sudoku')
module.exports = class DB{
    constructor(DB_HOST){
        this.DB_HOST = DB_HOST;
    }
    connect(){
        this.connection = mongoose.createConnection(this.DB_HOST)
        this.user = this.connection.model('User', userSchema)
        this.sudoku = this.connection.model('Sudoku', sudokuSchema)
        return {user: this.user, sudoku: this.sudoku}
    }
    close(){
        this.connection.close()
    }
}