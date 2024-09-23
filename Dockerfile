# Use Node.js image as the base image for the build stage
FROM node:18.16.0-alpine as build

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json (if available) to the working directory
COPY package.json ./
COPY package-lock.json ./

# Install dependencies
RUN npm install

# Copy the source code and configuration files
COPY src ./src
COPY tsconfig.json ./

# Build the application
RUN npm run build

COPY prisma ./prisma/

# Generate Prisma client
RUN npx prisma generate

# Use Node.js image for the production stage
FROM node:18.16.0-alpine as production

# Set the working directory
WORKDIR /app

# Copy node_modules and build output from the build stage
COPY --from=build /app/node_modules /app/node_modules
COPY --from=build /app/package.json /app/package.json
COPY --from=build /app/dist /app/dist
COPY --from=build /app/prisma /app/prisma 

# Expose port 8500
EXPOSE 8500

# Start the application
CMD ["npm", "run", "start"]
