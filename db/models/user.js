const mongoose = require('mongoose');

module.exports.userSchema = new mongoose.Schema({
    username: { 
        type: String, 
        required: true, 
        index: { unique: true } 
    },
    email: { 
        type: String, 
        required: true, 
        index: { unique: true } 
    },
    password: { 
        type: String, 
        required: true }
}, { timestamps: true })