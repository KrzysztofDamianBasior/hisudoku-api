const {checkPassword, encryptPassword} = require('../index')

describe("Tests encrypt functions", () => {

    it("checks if password is encrypted", async ()=>{

        const password="testPassword"
        const encrypted =  await encryptPassword(password)

        expect(encrypted).toBeDefined()
    })

    it("checks if passwords are matched correctly", async ()=>{
        const password="testPassword"
        const encrypted =  await encryptPassword(password)

        const isMatched1 = await checkPassword(password, encrypted)
        expect(isMatched1).toBeTruthy()

        const isMatched2 = await checkPassword("invalidPassword", encrypted)
        expect(isMatched2).toBeFalsy()
    })
})