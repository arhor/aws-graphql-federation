# GraphQL Federation Pet Project

## Overview

This is a pet project aimed at gaining practical experience with GraphQL Federation. The project is built with a
microservices architecture, where users can write posts and leave comments. The system consists of the following
services:

| Name                         | Description                                            | Status                                                                                                                             |
|------------------------------|--------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| **Users Service**            | Manages user information and authentication            | ![GitHub Actions](https://github.com/arhor/aws-graphql-federation/actions/workflows/app-service-users-CI.yml/badge.svg)            |
| **Posts Service**            | Handles the creation, updating, and retrieval of posts | ![GitHub Actions](https://github.com/arhor/aws-graphql-federation/actions/workflows/app-service-posts-CI.yml/badge.svg)            |
| **Comments Service**         | Manages comments on posts                              | ![GitHub Actions](https://github.com/arhor/aws-graphql-federation/actions/workflows/app-service-comments-CI.yml/badge.svg)         |
| **Scheduled Events Service** | Manages scheduled events                               | ![GitHub Actions](https://github.com/arhor/aws-graphql-federation/actions/workflows/app-service-scheduled-events-CI.yml/badge.svg) |

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

2. **Install JDK**:
    You could use any way of installation, for an example [SdkMan](https://sdkman.io/jdks)

3. **Install NVM**:
   Follow the instructions at [NVM GitHub page](https://github.com/nvm-sh/nvm) (
   or [NVM for Windows GitHub page](https://github.com/coreybutler/nvm-windows) for Windows users) to install NVM.

4. **Install Node.js**:
    ```sh
    nvm install 18.20.2
    nvm use 18.20.2

### Backend Setup

1. **Install Java**: Ensure you have Java 8 to 17 installed. Gradle will manage the required version (21) using the
   toolchain.

2. **Build and run the required service**:
    - Users service:
    ```shell
    ./gradlew :app-users-service:bootRun
    ```
    - Posts service:
    ```shell
    ./gradlew :app-posts-service:bootRun
    ```
    - Comments service:
    ```shell
    ./gradlew :app-comments-service:bootRun
    ```
    - Scheduled Events service:
    ```shell
    ./gradlew :app-scheduled-events-service:bootRun
    ```
### Gateway Setup

1. **Change current directory**:
   ```sh
   cd app-gateway
   ```

2. **Install Dependencies**:
    ```sh
    npm install
    ```

3. **Run the gateway**:
    ```sh
    npm serve
    ```

> **_NOTE:_**  Using gateway UI to play with GraphQL unsure that cookies are enabled in settings. 

### Frontend Setup

1. **Change current directory**:
   ```sh
   cd app-client-web
   ```

2. **Install Dependencies**:
    ```sh
    npm install
    ```

3. **Run the Frontend**:
    ```sh
    npm serve
    ```

### Docker Setup

1. **Install Docker**:
   Follow the instructions at [Docker's official website](https://docs.docker.com/get-docker/) to install Docker.

2. **Start Docker Containers**:
    ```sh
    ./start-supergraph
    ```

## Contributing

Feel free to fork this repository and submit pull requests. For major changes, please open an issue first to discuss
what you would like to change.
