require('dotenv').config()

//connect to database
const {DB} = require('./db')
const db = new DB()
const DB_HOST = process.env.DB_HOST;
const models = db.connect(DB_HOST)

//create server
const express = require('express');
const { ApolloServer} = require('apollo-server-express')

const port = process.env.PORT || 4000;

const helmet = require('helmet')
const cors = require('cors')

const app = express();

app.use(helmet())
app.use(cors())

const depthLimit = require('graphql-depth-limit')
const { createComplexityLimitRule } = require('graphql-validation-complexity')
const {schema, resolvers} = require('./graphql')
const {getDataFromJWT} = require('./utils/jwt')

const server = new ApolloServer({
    schema, resolvers,
    context: ({ req }) => {
        const token = req.headers.authorization;
        try{
            const user = getDataFromJWT(token)
            return { models, user }    
        } catch(e){
            return { models }    
        }
    },
    validationRules: [depthLimit(5), createComplexityLimitRule(1000)],
})

server.start().then(res => {
    server.applyMiddleware({ app, path: '/api' })
})

app.get('/', (req, res) => res.send('Server is running'))
app.listen(4000, () => console.log(`Server is running on ${port}`))