const app = require('./app')

require('dotenv').config()
const port = process.env.PORT || 4000;

app.listen(4000, () => console.log(`Server is running on ${port}`))