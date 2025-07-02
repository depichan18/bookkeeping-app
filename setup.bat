@echo off
echo ====================================
echo   BOOKKEEPING APPLICATION SETUP
echo ====================================
echo.

echo Checking Java installation...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java not found! Please install Java 21 or higher.
    pause
    exit /b 1
)

echo.
echo Checking Maven installation...
mvn -version
if %errorlevel% neq 0 (
    echo ERROR: Maven not found! 
    echo Please install Maven 3.6+ and add it to PATH.
    echo Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo.
echo Checking JavaFX installation...
if not exist "C:\Java\javafx-sdk-21.0.7\lib" (
    echo ERROR: JavaFX not found at C:\Java\javafx-sdk-21.0.7\lib
    echo Please download JavaFX 21 SDK from https://openjfx.io/
    echo And extract to C:\Java\javafx-sdk-21.0.7\
    pause
    exit /b 1
)

echo.
echo All prerequisites found! Compiling application...
mvn clean compile

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo SUCCESS: Application compiled successfully!
echo You can now run the application using run.bat
echo.
pause
