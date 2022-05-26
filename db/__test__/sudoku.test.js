const sudoku = require('../models/sudoku')
const user = require('../models/user')
const mongoose = require('mongoose')
const server = require('mongodb-memory-server')

let gate = false
const setUp = async () => {
    const dbServer = await server.MongoMemoryServer.create() 
    const uri = dbServer.getUri()

    await mongoose.connect(uri, {useNewUrlParser: true})
    gate = true
    return dbServer
}

const dropDatabase = async (dbServer) => {
    if(gate){
        await mongoose.connection.dropDatabase()
        await mongoose.connection.close()
        await dbServer.stop()
        gate = false
    }
}

const deleteCollections = async (dbServer) => {
    if(gate){
        const collections = mongoose.connection.collections

        for (const key in collections){
            const collection = collections[key]
            await collection.deleteMany()
        }
    }
}

let databaseServer = undefined
beforeAll(async () => {
  databaseServer = await setUp();
});

afterEach(async () => {
  await deleteCollections();
});

afterAll(async () => {
  await dropDatabase(databaseServer);
});

describe("Mongodb sudoku model", () => {
  it("should create and save sudoku successfully", async () => {
    
    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123"
    }

    const User = mongoose.model('User', user.userSchema)
    const testUser = await User.create(userData)

    const sudokuData = {
      content: "123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;",
      author: mongoose.Types.ObjectId(testUser.id),
      favoriteCount: 1,
      favoritedBy: [mongoose.Types.ObjectId(testUser.id)]
    }

    const Sudoku = mongoose.model('Sudoku', sudoku.sudokuSchema)
    const testSudoku = await Sudoku.create(sudokuData)

    expect(testSudoku._id).toBeDefined()
    expect(testSudoku.content).toBe(sudokuData.content)
    expect(testSudoku.author).toBe(sudokuData.author)
    expect(testSudoku.favoriteCount).toBe(sudokuData.favoriteCount)
    expect(testSudoku.favoritedBy).toEqual(sudokuData.favoritedBy)
  });

  it("should insert sudoku successfully, but the field not defined in the schema should be undefined", async () => {

    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123"
    }

    const User = mongoose.model('User', user.userSchema)
    const testUser = await User.create(userData)

    const sudokuData = {
      content: "123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;123.456.789;",
      author: mongoose.Types.ObjectId(testUser.id),
      favoriteCount: 5,
      favoritedBy: [mongoose.Types.ObjectId(testUser.id)],
      size: "9x9"
    }

    const Sudoku = mongoose.model('Sudoku', sudoku.sudokuSchema)
    const testSudoku = await Sudoku.create(sudokuData)

    expect(testSudoku._id).toBeDefined()
    expect(testSudoku.size).toBeUndefined()
  });

  it("should should failed after creating a sudoku without required field ", async () => {
    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123"
    }
    const User = mongoose.model('User', user.userSchema)
    const testUser = await User.create(userData)

    const sudokuData = {
      author: mongoose.Types.ObjectId(testUser.id),
      favoriteCount: 1,
      favoritedBy: [mongoose.Types.ObjectId(testUser.id)]
    }
    const Sudoku = mongoose.model('Sudoku', sudoku.sudokuSchema)

    let err = undefined
    try{
      const testSudoku = await Sudoku.create(sudokuData)  
    } catch(error){
      err = error
    }

    expect(err).toBeDefined()
    expect(err).toBeInstanceOf(mongoose.Error.ValidationError);
    expect(err.errors.content).toBeDefined();
  });
});