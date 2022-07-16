const logger = require('pino')
const dayjs = require('dayjs')

//ERROR, INFO, DEBUG, WARN, FATAL   log.info(``)
const logs = logger({
  base: { pid: false },
  timestamp: () => `, "time":${dayjs().format()}`
})

module.exports = logs
