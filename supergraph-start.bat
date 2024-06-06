@rem **************************************************************************
@rem *        Script to build and deploy subgraph services for Windows        *
@rem **************************************************************************

gradlew :fullBuild && docker compose up --build
