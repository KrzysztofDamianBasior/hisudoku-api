# https://www.tomray.dev/nestjs-docker-production
###################
# BUILD FOR LOCAL DEVELOPMENT - This is the stage where we build the image for local development.
###################

FROM node:18-alpine As development

WORKDIR /usr/src/app

# Copy application dependency manifests to the container image.
# A wildcard is used to ensure copying both package.json AND package-lock.json (when available).
# Copying this first prevents re-running npm install on every code change.
COPY --chown=node:node package*.json ./

# npm ci is similar to npm install, except it's meant to be used in automated environments such as test platforms, continuous integration, and deployment -- or any situation where you want to make sure you're doing a clean install of your dependencies.
# Install app dependencies using the `npm ci` command instead of `npm install`
# RUN npm ci
# if you use yarn.lock run yarn install instead
RUN yarn install --frozen-lockfile

# Whenever you use the COPY instruction, it's also good practice to add a flag to ensure the user has the correct permissions. You can achieve this by using --chown=node:node whenever you use the COPY instruction
# Bundle app source
COPY --chown=node:node . .

# By default, if you don't specify a USER instruction in your Dockerfile, the image will run using the root permissions. This is a security risk, so we'll add a USER instruction to our Dockerfile. The node image we're using already has a user created for us called node
# Use the node user from the image (instead of the root user)
USER node

###################
# BUILD FOR PRODUCTION - This is the stage where we build the image for production.
###################

FROM node:18-alpine As build

WORKDIR /usr/src/app

COPY --chown=node:node package*.json ./

# In order to run `npm run build` we need access to the Nest CLI which is a dev dependency. In the previous development stage we ran `npm ci` which installed all dependencies, so we can copy over the node_modules directory from the development image
COPY --chown=node:node --from=development /usr/src/app/node_modules ./node_modules

COPY --chown=node:node . .

# Run the build command which creates the production bundle
RUN yarn run build

# Set NODE_ENV environment variable
ENV NODE_ENV production

# Running `npm ci` removes the existing node_modules directory and passing in --only=production ensures that only the production dependencies are installed. This ensures that the node_modules directory is as optimized as possible
# RUN npm ci --only=production && npm cache clean --force

USER node

###################
# PRODUCTION  - We copy over the relevant production build files and start the server.
###################

FROM node:18-alpine As production

# Copy the bundled code from the build stage to the production image
COPY --chown=node:node --from=build /usr/src/app/node_modules ./node_modules
COPY --chown=node:node --from=build /usr/src/app/dist ./dist

# Start the server using the production build
CMD [ "node", "dist/main.js" ]

# docker build -t "nestjs-docker:0.0.1" .
# docker images 
# docker run  --env-file ./.env --rm -p 3000:3000 nestjs-docker:0.0.1