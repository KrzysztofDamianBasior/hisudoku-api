module.exports.nested = {
    Sudoku: {
        author: async (sudoku, args, { models }) => { return await models.User.findById(sudoku.author).select('-email') },
        favoritedBy: async (sudoku, args, { models }) => { return await models.User.find({ _id: { $in: sudoku.favoritedBy } }).select('-email') }
    },
    User: {
        sudokus: async (user, args, { models }) => { return await models.Sudoku.find({ author: user._id }).sort({ _id: -1 }) },
        favorites: async (user, args, { models }) => { return await models.Sudoku.find({ favoritedBy: user._id }).sort({ _id: -1 }) }
    },
    Me: {
        sudokus: async (user, args, { models }) => { return await models.Sudoku.find({ author: user._id }).sort({ _id: -1 }) },
        favorites: async (user, args, { models }) => { return await models.Sudoku.find({ favoritedBy: user._id }).sort({ _id: -1 }) }
    }
}