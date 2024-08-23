@rem **************************************************************************
@rem *        Script to build and deploy subgraph services for Windows        *
@rem **************************************************************************

@echo off

setlocal

set SKIP_TEST=-Pskip-test
set GRADLE_BUILD_COMMAND=
set DOCKER_BUILD_COMMAND=

:argsLoopStart

if "%1"=="" goto argsLoopEnd
if "%1"=="--test" set SKIP_TEST=
if "%1"=="--build" (
    set GRADLE_BUILD_COMMAND=gradlew :fullBuild
    set DOCKER_BUILD_COMMAND=--build
)
shift
goto argsLoopStart

:argsLoopEnd

if not "%GRADLE_BUILD_COMMAND%"=="" (
    set GRADLE_BUILD_COMMAND=%GRADLE_BUILD_COMMAND% %SKIP_TEST% &&
)

call %GRADLE_BUILD_COMMAND% docker compose up %DOCKER_BUILD_COMMAND%

endlocal
