const {AuthenticationError} = require("apollo-server-express")

module.exports = {
        sudokus: async (parent, args, { models }) => { return await models.Sudoku.find() },
        sudoku: async (parent, args, { models }) => { return await models.Sudoku.findById(args.id) },
        user: async (parent, { username }, { models, user }) => { 
            
            if (!user) {
                throw new AuthenticationError('Only for logged in')
            }

            return await models.User.findOne({ username }).select('-email') 
        },
        users: async (parent, args, { models, user }) => { 
            
            if (!user) {
                throw new AuthenticationError('Only for logged in')
            }

            return await models.User.find({}).select('-email') 
        },
        me: async (parent, args, { models, user }) => { 

            if (!user) {
                throw new AuthenticationError('Only for logged in')
            }

            return await models.User.findById(user.id) 
        },
        sudokuFeed: async (parent, { cursor }, { models }) => {

            //maximum amount of sudoku returned
            const limit = 10

            let hasNextPage = false

            //if no cursor has been passed, the default query will be empty, it will retrieve the latest notes from the database
            let cursorQuery = {}

            //if a cursor has been passed, the query will look for notes whose ObjectId value is less than the cursor value
            if (cursor) {
                cursorQuery = { _id: { $lt: cursor } }
            }

            let sudokus = await models.Sudoku.find(cursorQuery).sort({ _id: -1 }).limit(limit + 1);

            if (sudokus.length > limit) {
                hasNextPage = true;
                sudokus = sudokus.slice(0, -1)
            }

            //the cursor is the mongo identifier of the last element in the array
            const newCursor = sudokus[sudokus.length - 1]._id

            return { sudokus, cursor: newCursor, hasNextPage }
        }
    }