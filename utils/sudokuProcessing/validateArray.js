module.exports = (array, emptyCharacter='0') => {
    const digits = array.filter((character) => character !== emptyCharacter);
    return digits.length === [...new Set(digits)].length;
}