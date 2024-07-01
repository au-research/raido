# Base image
FROM public.ecr.aws/docker/library/node:20.14.0-bookworm-slim
RUN apt update && apt upgrade -y && apt install -y wget
# Create app directory
WORKDIR /usr/local/bff

# A wildcard is used to ensure both package.json AND package-lock.json are copied
COPY package*.json ./

# Install app dependencies
RUN npm install

# Bundle app source
COPY . .

# Copy the .env and .env.development files
#COPY .env .env.development ./

# Creates a "dist" folder with the production build
RUN npm run build

# Expose the port on which the app will run
EXPOSE 3000

# Start the server using the production build
CMD ["npm", "run", "start:prod"]