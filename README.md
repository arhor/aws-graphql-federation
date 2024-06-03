# GraphQL Federation Pet Project

## Overview

This is a pet project aimed at gaining practical experience with GraphQL Federation. The project is built with a
microservices architecture, where users can write posts and leave comments. The system consists of three main
services:

- **Users Service**: Manages user information and authentication
- **Posts Service**: Handles the creation, updating, and retrieval of posts
- **Comments Service**: Manages comments on posts

all of which are subgraphs in the overall GraphQL federation supergraph.

## Backend Stack

- **Kotlin**
- **Java** (Version: 21)
- **Spring Boot**
- **Netflix DGS** (Domain Graph Service)
- **AWS SNS/SQS** (for messaging)
- **Postgres** (for database)
- **Gradle** (for build automation)

## Frontend Stack

- **TypeScript**
- **React.js**
- **React Material** (for UI components)
- **Apollo Client** (for GraphQL client-side operations)

## Prerequisites

### Java

- **Java Version**: 21
- Gradle can work with any Java version from 8 to 17. Gradle will automatically download and use the required Java
  version (21) using the toolchain.

### Node.js

- **Node Version**: 18.20.2
- It is recommended to use **NVM (Node Version Manager)** for managing Node.js versions.

### Docker

- Docker is used to set up the necessary infrastructure. Localstack is used to replicate AWS APIs locally, and Postgres
  is used directly.

## Setup Instructions

### General setup

1. **Clone the Repository**:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

### Backend Setup

1. **Install Java**: Ensure you have Java 8 to 17 installed. Gradle will manage the required version (21) using the
   toolchain.

2. **Build the required service**:
    ```sh
    ./gradlew :{service-name}:build
    ```
   example:
   ```shell
   ./gradlew :app-users-service:build
   ```

3. **Run the required Service**:
    ```sh
    ./gradlew :{service-name}:bootRun
    ```
   example:
   ```shell
   ./gradlew :app-users-service:bootRun
   ```

### Frontend Setup

1. **Install NVM**:
   Follow the instructions at [NVM GitHub page](https://github.com/nvm-sh/nvm) (
   or [NVM for Windows GitHub page](https://github.com/coreybutler/nvm-windows) for Windows users) to install NVM.

2. **Install Node.js**:
    ```sh
    nvm install 18.20.2
    nvm use 18.20.2
    ```
3. **Change current directory**:
   ```sh
   cd app-client-web
   ```

4. **Install Dependencies**:
    ```sh
    npm install
    ```

5. **Run the Frontend**:
    ```sh
    npm serve
    ```

### Docker Setup

1. **Install Docker**:
   Follow the instructions at [Docker's official website](https://docs.docker.com/get-docker/) to install Docker.

2. **Start Docker Containers**:
    ```sh
    ./supergraph-start
    ```

## Contributing

Feel free to fork this repository and submit pull requests. For major changes, please open an issue first to discuss
what you would like to change.
