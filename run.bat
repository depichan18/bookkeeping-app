@echo off
echo Starting Bookkeeping Application...
echo.

REM Set JavaFX module path
set JAVAFX_PATH=C:\Java\javafx-sdk-21.0.7\lib

REM Run the application with JavaFX modules
mvn clean javafx:run -f pom.xml

echo.
echo Application finished.
pause
