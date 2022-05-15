const bcrypt = require('bcrypt')

const saltRounds = 10
module.exports.encryptPassword = async password => {    
    return await bcrypt.hash(password, saltRounds)
}
module.exports.checkPassword = async (plainText, hashed) => {
    return await bcrypt.compare(plainText, hashed)
}