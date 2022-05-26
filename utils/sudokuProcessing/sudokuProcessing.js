module.exports = (str) => {
    return /^([0-9]{3}\.[0-9]{3}\.[0-9]{3};){9}$/.test(str)
}