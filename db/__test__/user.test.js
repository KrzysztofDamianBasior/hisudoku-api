const user = require('../models/user')
const mongoose = require('mongoose')
const server = require('mongodb-memory-server')


let gate = false

const setUp = async () => {
  const dbServer = await server.MongoMemoryServer.create() 
  const uri = dbServer.getUri()

  await mongoose.connect(uri, {useNewUrlParser: true})
  gate = true
  return dbServer
}

const dropDatabase = async (dbServer) => {
  if(gate){
      await mongoose.connection.dropDatabase()
      await mongoose.connection.close()
      await dbServer.stop()
      gate = false
  }
}

const deleteCollections = async (dbServer) => {
  if(gate){
      const collections = mongoose.connection.collections

      for (const key in collections){
          const collection = collections[key]
          await collection.deleteMany()
      }
  }
}


let databaseServer = undefined
beforeAll(async () => {
databaseServer = await setUp();
});

afterEach(async () => {
await deleteCollections();
});

afterAll(async () => {
await dropDatabase(databaseServer);
});


describe("Test user model", () => {
  it("create and save user successfully", async () => {

    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123"
    }
    const User = mongoose.model('User', user.userSchema)
    const testUser = await User.create(userData)

    expect(testUser._id).toBeDefined();
    expect(testUser.email).toBe(userData.email);
    expect(testUser.password).toBe(userData.password);
  });

  it("insert user successfully, but the field not defined in schema should be undefined", async () => {
    
    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123",
        phone: "123456789"
    }
    const User = mongoose.model('User', user.userSchema)
    const testUser = await User.create(userData)

    expect(testUser._id).toBeDefined();
    expect(testUser.phone).toBeUndefined();
  });

  it("create user without required field should failed", async () => {
    let err = undefined
    const userData = {
        username: "testName",
        password: "123",
    }
    const User = mongoose.model('User', user.userSchema)
    try{
        const testUser = await User.create(userData)
    } catch(error){
        err = error
    }
 
    expect(err).toBeDefined()
    expect(err).toBeInstanceOf(mongoose.Error.ValidationError)
    expect(err.errors.email).toBeDefined()
  });
});