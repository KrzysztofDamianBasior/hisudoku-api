const jwt = require('jsonwebtoken')

module.exports.generateJWT = async user => {
    return await jwt.sign({ id: user.id }, process.env.JWT_SECRET)
}

module.exports.getDataFromJWT = async JWT => {
    if (JWT) {
        try {
            return await jwt.verify(JWT, process.env.JWT_SECRET)
        } catch (err) {
            throw new Error('invalid session')
        }
    }
}