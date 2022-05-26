/* eslint-disable no-undef */
const mutation = require('../mutation')
const mongoose = require('mongoose')
const MockDb = require('../../../utils/testingTools/MockDb')
const sudokuSchema = require('../../../db/models/sudoku')
const userSchema = require('../../../db/models/user')
const {encryptPassword, checkPassword} = require("../../../utils/encryption")
require('dotenv').config()


let databaseServer = undefined
let models = undefined
let testUser = undefined
let testSudoku = undefined

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
    favoritedBy: [mongoose.Types.ObjectId(testUser1.id)]
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
  await sudokuModel.create([sudoku1, sudoku2, sudoku3])
});

afterAll(async () => {
  await databaseServer.deleteCollections();
  await databaseServer.dropDatabase();
});


beforeEach(async ()=>{
  const testUserData = { 
    username: "testUsername",
    email: "testEmail@gmail.com",
    password: await encryptPassword("123")
  }
  testUser = await models.User.create(testUserData)

  const testSudokuData = {
    content: "000.000.006;300.000.000;900.000.400;000.000.000;100.000.000;000.000.000;000.070.000;000.500.000;000.000.200;",
    author: mongoose.Types.ObjectId(testUser.id),
    favoriteCount: 1,
    favoritedBy: [mongoose.Types.ObjectId(testUser.id)]
  }
  testSudoku = await models.Sudoku.create(testSudokuData)
})
afterEach(async ()=>{
  // eslint-disable-next-line no-empty
  try {testUser.remove()} catch {}
  // eslint-disable-next-line no-empty
  try {testSudoku.remove()} catch {}
})


describe("GQL's mutation", ()=>{

  it('should add new sudoku to database', async ()=>{

    const sudokuContent = "000.010.009;300.000.000;000.000.400;000.000.000;100.000.000;000.000.000;000.000.000;000.500.000;000.000.200;"
    const newSudoku = await mutation.newSudoku(
      undefined, 
      {content: sudokuContent}, 
      {models, user: testUser})

  //  const sudoku = await models.Sudoku.find({author: testUser.id}) 
  //  expect(sudoku).toBeDefined
    expect(newSudoku.content).toEqual(sudokuContent)   
  })
  

  it('should delete sudoku from database', async ()=>{
    const status = await mutation.deleteSudoku(undefined, {id: testSudoku.id} , {models, user: testUser} )
    expect(status).toBeTruthy()
  })

  it('should update sudoku in database', async ()=>{
    const content = "100.000.000;200.000.000;300.000.000;400.000.000;500.000.000;600.000.000;700.000.000;800.000.000;900.000.000;"
    const id = testSudoku.id
    const user = {id: testUser.id}
    const result = await mutation.updateSudoku(undefined, {content, id}, {models,user})
    expect(result.content).toEqual(content)
  })

  it('should sign up an user and return proper jwt token', async ()=>{
    const testUser = {
      username: "testing",
      email: "testing@gmail.com",
      password: "abcdefghijk"
    }
    const jwt = await mutation.signUp(undefined, testUser, {models})
    expect(jwt).toBeDefined()
    expect(jwt).toMatch(/^[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*$/)
  })

  it('should sign in an user and return proper jwt token', async ()=>{
    const testUser = { 
      username: "testUsername",
      email: "testEmail@gmail.com",
      password: "123"
    }
    
    const jwt = await mutation.signIn(undefined, testUser, {models})
    expect(jwt).toBeDefined()
    expect(jwt).toMatch(/^[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*$/)
  })

  it("should should toggle sudoku's favorite indicator", async ()=>{
    const isFavorite = testSudoku.favoritedBy.includes(testUser.id)
    const result = await mutation.toggleFavorite(undefined, testSudoku, {models, user: testUser})
    const sudoku = await models.Sudoku.findById(result._id)
    expect(sudoku.favoritedBy.includes(testUser.id) === !isFavorite).toBeTruthy
  })
})