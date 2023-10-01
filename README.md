## Description

A system for publishing and managing sudoku puzzles using the nest framework and node runtime.
## Features

* API compatible with the graphql architecture
* Documented mutations and queries
* Role-based access controll
* User registration and login with JWT authentication
* Password encryption using BCrypt
* Password recovery service using the node-mailjet client
  Communication with the MongoDB using the mongoose library

## Installation

```bash
$ yarn install
```

## Running the app

```bash
# development
$ yarn run start

# watch mode
$ yarn run start:dev

# production mode
$ yarn run start:prod
```

## Test

```bash
# unit tests
$ yarn run test

# e2e tests
$ yarn run test:e2e

# test coverage
$ yarn run test:cov
```

## License

[MIT licensed](LICENSE).

## Reference Documentation
For further reference, please consider the following sections:

* [Official Nest documentation](https://docs.gradle.org)
* [Official Mongoose documentation](https://mongoosejs.com/docs/)
* [Official node-mailjet documentation](https://github.com/mailjet/mailjet-apiv3-nodejs#readme)
