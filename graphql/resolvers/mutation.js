const mongoose = require('mongoose')
const {AuthenticationError, ForbiddenError, UserInputError} = require("apollo-server-express")
const {encryptPassword, checkPassword} = require("../../utils/encryption")
const {generateJWT} = require("../../utils/jwt")
const {isSudokuValid} = require('../../utils/sudokuProcessing')

module.exports = {
    newSudoku: async (parent, args, { models, user }) => {

        if (!user) {
            throw new AuthenticationError('Only for logged in')
        }
        if(!isSudokuValid(args.content)){
            throw new UserInputError('Invalid argument value');
        }

        return await models.Sudoku.create({
            content: args.content,
            author: mongoose.Types.ObjectId(user.id)
        });
    },
    deleteSudoku: async (parent, { id }, { models, user }) => {

        if (!user) {
            throw new AuthenticationError('Only for logged in')
        }

        const sudoku = await models.Sudoku.findById(id);
        
        if (sudoku && String(sudoku.author) !== user.id) {
            throw new ForbiddenError('No permission')
        }

        try {
            await sudoku.remove()
            return true
        } catch (err) {
            return false
        }
    },
    updateSudoku: async (parent, { content, id }, { models, user }) => {

        if (!user) {
            throw new AuthenticationError('Only for logged in')
        }
        const sudoku = await models.Sudoku.findById(id)

        if (sudoku && String(sudoku.author) !== user.id) {
            throw new ForbiddenError('No permission')
        }

        return await models.Sudoku.findOneAndUpdate(
            { _id: id, },
            { $set: { content } },
            { new: true }
        )
    },

    signUp: async (parent, { username, email, password }, { models }) => {
        email = email.trim().toLowerCase();
        const hashed = await encryptPassword(password)

        try {
            const user = await models.User.create({
                username,
                email,
                password: hashed
            });
            return generateJWT(user)
        } catch (err) {
            throw new Error('Creating account error')
        }
    },

    signIn: async (parent, { username, email, password }, { models }) => {
        if (email) {
            email = email.trim().toLowerCase()
        }

        const user = await models.User.findOne({ $or: [{ email }, { username }] })
        const valid = await checkPassword(password, user.password)

        if (!valid) {
            throw new AuthenticationError('Only for logged in')
        }
        return generateJWT(user)
    },

    toggleFavorite: async (parent, { id }, { models, user }) => {

        if (!user) {
            throw new AuthenticationError('Only for logged in');
        }

        let sudokuCheck = await models.Sudoku.findById(id);
        const hasUser = sudokuCheck.favoritedBy.indexOf(user.id)

        if (hasUser >= 0) {
            return await models.Sudoku.findByIdAndUpdate(
                id,
                { $pull: { favoritedBy: mongoose.Types.ObjectId(user.id) }, $inc: { favoriteCount: -1 } },
                { new: true })
        } else {
            return await models.Sudoku.findByIdAndUpdate(
                id,
                { $push: { favoritedBy: mongoose.Types.ObjectId(user.id) }, $inc: { favoriteCount: 1 } },
                { new: true }
            )
        }
    },
}