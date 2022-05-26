/* eslint-disable no-undef */
const query = require('../query')
const mongoose = require('mongoose')
const MockDb = require('../../../utils/testingTools/MockDb')
const sudokuSchema = require('../../../db/models/sudoku')
const userSchema = require('../../../db/models/user')

let databaseServer = undefined
let models = undefined

beforeAll(async () => {

  databaseServer = new MockDb()
  await databaseServer.setup();
  const userModel = databaseServer.connectModel('User', userSchema)
  const sudokuModel = databaseServer.connectModel('Sudoku', sudokuSchema)
  models = {User: userModel,Sudoku: sudokuModel}

  const user1 = { 
    username: "testName1",
    email: "testEmail1@gmail.com",
    password: "123"
  }
  const user2 = { 
      username: "testName2",
      email: "testEmail2@gmail.com",
      password: "123"
  }
  const user3 = {
      username: "testName3",
      email: "testEmail3@gmail.com",
      password: "123"
  }
  const [testUser1, testUser2, testUser3] = await userModel.create([user1, user2, user3])

  const sudoku1 = {
    content: "000.010.007;300.000.000;000.000.400;000.000.000;000.600.000;000.000.000;900.000.000;000.500.000;000.000.200;",
    author: mongoose.Types.ObjectId(testUser1.id),
    favoriteCount: 1,
    favoritedBy: [mongoose.Types.ObjectId(testUser3.id)]
  }
  const sudoku2 = {
    content: "000.010.009;000.000.000;000.000.400;000.000.000;000.000.000;000.000.000;000.000.000;000.500.000;000.000.200;",
    author: mongoose.Types.ObjectId(testUser2.id),
    favoriteCount: 1,
    favoritedBy: [mongoose.Types.ObjectId(testUser3.id)]
  }
  const sudoku3 = {
    content: "000.000.006;300.000.000;900.000.400;000.000.000;100.000.000;000.000.000;000.070.000;000.500.000;000.000.200;",
    author: mongoose.Types.ObjectId(testUser3.id),
    favoriteCount: 1,
    favoritedBy: [mongoose.Types.ObjectId(testUser2.id)]
  }
  const [testSudoku1, testSudoku2, testSudoku3] = await sudokuModel.create([sudoku1, sudoku2, sudoku3])

});

afterAll(async () => {
  await databaseServer.deleteCollections();
  await databaseServer.dropDatabase();
});


describe("GQL's query", ()=>{

    it("should return a set of sudokus", async ()=>{
        const sudokus = await query.sudokus(undefined, undefined, { models })
        expect(sudokus).toBeDefined()
        expect(sudokus).toHaveLength(3)
    })

    it("should return one sudoku", async ()=>{
        const {User: userModel, Sudoku: sudokuModel} = models
        let sudokuFromDb = await sudokuModel.find({})
        sudokuFromDb = sudokuFromDb[0]

        const sudoku = await query.sudoku(undefined, {id: sudokuFromDb}, {models} )
        expect(sudoku).toBeDefined()
        expect(sudoku.content).toEqual("000.010.007;300.000.000;000.000.400;000.000.000;000.600.000;000.000.000;900.000.000;000.500.000;000.000.200;")
    })

    it("should return an user", async ()=>{
        const user = await query.user(undefined, {username: "testName2"}, {models, user: {username: "test"}}) 
        expect(user).toBeDefined()
        expect(user.username).toEqual("testName2")
    })

    it("should return a set of users", async ()=>{
        const users = await query.users(undefined,undefined, {models, user: {username: "test"}})
        expect(users).toBeDefined()
        expect(users).toHaveLength(3)
    })

    it("should return user data", async ()=>{
        const accountOwner = await models.User.find({username: "testName1"})
        
        const testA = await models.User.findById(accountOwner[0]._id) 
        const mee = await query.me(undefined,undefined,{models, user:{id: accountOwner[0]._id}})
        expect(mee).toBeDefined()
        expect(mee.email).toEqual("testEmail1@gmail.com")
    })

    it("it should return cursor, boolean value and a set of sudokus", async ()=>{
        const sudokuFeed = await query.sudokuFeed(undefined, {}, {models})

        expect(sudokuFeed).toBeDefined()
        expect(sudokuFeed.sudokus).toHaveLength(3)
        expect(sudokuFeed.hasNextPage).toBeFalsy()
    })
})