
/*
    Gather all  functionalities related to gql resolvers and re-export them
*/
const { GraphQLDateTime } = require('graphql-iso-date')
const { Query } = require("./query")
const { Mutation } = require("./mutation")
const { nested } = require("./nested")

const resolvers = {Query, Mutation, ...nested,  DateTime: GraphQLDateTime}

module.exports = resolvers