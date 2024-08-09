@rem **************************************************************************
@rem *        Script to build and deploy subgraph services for Windows        *
@rem **************************************************************************

@echo off

setlocal

set SKIP_TEST=-Pskip-test

:argsLoopStart

if "%1"=="" goto argsLoopEnd
if "%1"=="--test" set SKIP_TEST=
shift
goto argsLoopStart

:argsLoopEnd

gradlew :fullBuild %SKIP_TEST% && docker compose up --build

endlocal
