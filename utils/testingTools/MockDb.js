const mongoose = require('mongoose')
const server = require('mongodb-memory-server')

module.exports = class MockDb {
    
    constructor(){
        this.gate = false
        this.collections = []
    }

    async setup(){
        this.dbServer = await server.MongoMemoryServer.create() 
        const uri = this.dbServer.getUri()
        this.connection = mongoose.createConnection(uri)
        this.gate = true
        return this.dbServer
    }

    connectModel(modelName, modelSchema){
        if(this.gate){
            let dbModel = this.connection.model(modelName, modelSchema)
            this.collections.push(dbModel)
            return dbModel
        }
    }

    async dropDatabase(){
        if(this.gate){
            await this.connection.dropDatabase()
            await this.connection.close()
            await this.dbServer.stop()
            this.gate = false
        }
    }
    async deleteCollections(){
        if(this.gate){
            const collections = this.collections
        
            for (const key in collections){
                const collection = collections[key]
                await collection.deleteMany()
            }
        }        
    }
}