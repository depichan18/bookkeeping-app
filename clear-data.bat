@echo off
echo ========================================
echo   CLEARING ALL DATABASE DATA
echo ========================================
echo.

echo WARNING: This will delete ALL data from the database!
set /p confirm="Are you sure? (y/N): "
if /i not "%confirm%"=="y" (
    echo Operation cancelled.
    pause
    exit /b 0
)

echo.
echo Clearing database...
mvn compile exec:java -Dexec.mainClass="com.bookkeeping.util.SampleDataLoader" -Dexec.args="--clear"

echo.
echo Database cleared!
pause
