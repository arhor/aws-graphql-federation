@rem **************************************************************************
@rem *        Script to build and deploy subgraph services for Windows        *
@rem **************************************************************************

gradlew :app-service-users:build :app-service-posts:build :app-service-comments:build &&^
 docker compose up --build
