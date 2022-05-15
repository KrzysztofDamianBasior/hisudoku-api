const {generateJWT, getDataFromJWT} = require('../index')
process.env.JWT_SECRET = "testSecret"

describe("Use of jwt in the login scenario", () => {

    it("checks that the token is generated correctly", async ()=>{
        const user = {id: "000"}
        const jwt = await generateJWT(user)

        expect(jwt).toBeDefined()
        expect(jwt).toMatch(/^[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*$/)
    })

    it("checks if the token is correctly read", async ()=>{
        const user = {id: "000"}
        const jwt = await generateJWT(user)
        const userData = await getDataFromJWT(jwt)

        expect(userData).toBeDefined()
        expect(userData.id).toBeDefined()
        expect(userData.id).toMatch(/000/)
    })

    it("checks if an invalid token throws an exception", async ()=>{
        const mockJWT = "000"
        expect( () => getDataFromJWT(mockJWT) ).rejects.toThrow(Error)
    })
})