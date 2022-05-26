const userSchema = require('../user')
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

describe("Mongodb user model", () => {
  it("should create and save user successfully", async () => {

    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123"
    }
    const User = mongoose.model('User', userSchema)
    const testUser = await User.create(userData)

    expect(testUser._id).toBeDefined();
    expect(testUser.email).toBe(userData.email);
    expect(testUser.password).toBe(userData.password);
  });

  it("should insert user successfully, but the field not defined in schema should be undefined", async () => {
    
    const userData = {
        username: "testName",
        email: "testEmail@gmail.com",
        password: "123",
        phone: "123456789"
    }
    const User = mongoose.model('User', userSchema)
    const testUser = await User.create(userData)

    expect(testUser._id).toBeDefined();
    expect(testUser.phone).toBeUndefined();
  });

  it("should failed after creating user without required field", async () => {
    let err = undefined
    const userData = {
        username: "testName",
        password: "123",
    }
    const User = mongoose.model('User', userSchema)
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