const EasyGraphQLTester = require('easygraphql-tester')
const {schema} = require('../index')
const { gql } = require('apollo-server-express')

describe("Query gql typedef", () => {

  let tester
  beforeAll(()=>{
    tester = new EasyGraphQLTester(schema)
  })

  it("should get all sudokus", () => {      
    const GET_SUDOKUS =`
      query GET_SUDOKUS {
        sudokus{
          id
          content
          author{
            id
            username
          }
          createdAt
          updatedAt
          favoriteCount
          favoritedBy{
            id
            username
          }     
        }
      }
    ` 
    tester.test(true, GET_SUDOKUS);
  })

  it("should get a single sudoku by id", () => {      
    const GET_SUDOKU = `
      query GET_SUDOKU($id: ID!){
        sudoku(id: $id){
          id
          content
          author{
            id
            username
          }
          createdAt
          updatedAt
          favoriteCount
          favoritedBy{
            id
            username
          }     
        }
      }
    `
    tester.test(true, GET_SUDOKU, {id: "ObjectID"});
  });

  it("should get user's profile details", () => {          
    const GET_ME =`
      query GET_ME {
        me {
          id 
          username 
          email
          favorites {
            id 
            createdAt 
            content 
            favoriteCount 
            author {
              id 
              username
            }
          }
          sudokus {
            id 
            createdAt 
            content 
            favoriteCount
          }
        }
      }
    `
    tester.test(true, GET_ME);
  });

  it("should get user's data", () => {          
    const GET_USER =`
      query GET_USER($username: String!) {
        user(username: $username) {
          id 
          username 
          sudokus {
            id 
            createdAt 
            content 
            favoriteCount
        }
      }
    }
    `
    tester.test(true, GET_USER, {username: "testName"});
  });

  it("should get set of sudokus", () => {          
    const SUDOKU_FEED =`
      query SUDOKU_FEED {
        sudokuFeed {
          sudokus{
            id 
            createdAt
            content
            favoriteCount
          }
          cursor
          hasNextPage
        }
      }
      `
    tester.test(true, SUDOKU_FEED);
  });

  it("should get all users", () => {          
    const GET_USERS =`
      query GET_USERS {
        users {
          id 
          username
        }
      }
      `
    tester.test(true, GET_USERS);
  });

})

describe("Mutation gql typedef", () => {

  let tester
  beforeAll(()=>{
    tester = new EasyGraphQLTester(schema)
  })

  it("should update sudoku", () => {
    const UPDATE_SUDOKU =`
      mutation UPDATE_SUDOKU($id: ID!, $content: String!){
        updateSudoku(id: $id, content: $content){
          id
          content
          createdAt
          updatedAt
        }
      }
    `
    tester.test(true, UPDATE_SUDOKU, {
      id: "id123",
      content: ""
    })
  })

  it("should delete sudoku", () => {
    const DELETE_SUDOKU =`
      mutation DELETE_SUDOKU($id: ID!){
        deleteSudoku(id: $id)
      }
    `
    tester.test(true, DELETE_SUDOKU, {id: "id123"})
  })

  it("should create sudoku", () => {
    const CREATE_SUDOKU =`
      mutation CREATE_SUDOKU($content: String!){
        newSudoku(content: $content){
          id
          content
          createdAt
          updatedAt
        }
      }
    `
    tester.test(true, CREATE_SUDOKU, {content: "100.000.000;"})
  })

  it("should perform user's sign-in", () => {
    const SIGNIN_USER =`
      mutation signIn($email: String, $password: String!){
        signIn(email: $email, password: $password)
      }
    `
    tester.test(true, SIGNIN_USER, {email: "testEmail@gmail.com", password: "testPassword"})
  })

  it("should perform user's signup", () => {
    const SIGNUP_USER =`
      mutation SIGNUP_USER($username: String!, $email: String!, $password: String!){
        signUp(email: $email, username: $username, password: $password)
      }
    `
    tester.test(true, SIGNUP_USER, {
      email: "testEmail@gmail.com",
      username: "testUsername",
      password: "testPassword",
    })
  })

  it("should toggle favorite for selected sudoku", () => {
    const TOGGLE_FAVORITE =`
      mutation TOGGLE_FAVORITE($id: ID!){
        toggleFavorite(id: $id){
          id 
          favoriteCount
        }
      }
    `
    tester.test(true, TOGGLE_FAVORITE, {id: "id123"})
  })
})
