const { gql } = require('apollo-server-express')

module.exports.schema = gql`
scalar DateTime

type User {
    id: ID!
    username: String!
    sudokus: [Sudoku!]!
}

type Me {
    id: ID!
    username: String!
    email: String!
    sudokus: [Sudoku!]!
    favorites: [Sudoku!]!
}

type Sudoku {
    id: ID!
    content: String!
    author: User!
    createdAt: DateTime!
    updatedAt: DateTime!
    favoriteCount: Int!
    favoritedBy: [User!]
}

type SudokuFeed {
    sudokus: [Sudoku]!
    cursor: String!
    hasNextPage: Boolean!
}

type Query {
    sudokus: [Sudoku!]!
    sudoku(id: ID): Sudoku!
    user(username: String!): User 
    users: [User!]!
    me: Me!    
    sudokuFeed(cursor: String): SudokuFeed
}

type Mutation {
    newSudoku(content: String!): Sudoku!
    updateSudoku(id: ID!, content: String!): Sudoku!
    deleteSudoku(id: ID!): Boolean!

    signUp(username: String!, email: String!, password: String!): String!
    signIn(username: String, email: String, password: String!): String! 

    toggleFavorite(id: ID!): Sudoku!
}
`