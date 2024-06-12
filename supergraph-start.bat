@rem **************************************************************************
@rem *        Script to build and deploy subgraph services for Windows        *
@rem **************************************************************************

gradlew :fullBuild -Pskip-test && docker compose up --build
