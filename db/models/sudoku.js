const mongoose = require('mongoose');

module.exports.sudokuSchema = new mongoose.Schema({
    content: {
        type: String,
        required: true
    },
    author: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    favoriteCount: {
        type: Number,
        default: 0,
    },
    favoritedBy: [
        {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User'
        }
    ]
}, { timestamps: true })
