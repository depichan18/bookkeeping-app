@echo off
echo =================================================
echo         BOOKKEEPING DATA IMPORTER
echo =================================================
echo.

cd /d "%~dp0"

echo Checking if CSV files exist...
if not exist "accounts.csv" (
    echo ERROR: accounts.csv not found!
    echo Please make sure accounts.csv is in the project root directory.
    pause
    exit /b 1
)

if not exist "transactions.csv" (
    echo ERROR: transactions.csv not found!
    echo Please make sure transactions.csv is in the project root directory.
    pause
    exit /b 1
)

echo ✓ CSV files found!
echo.

echo Starting Maven compilation...
call mvn compile -q
if errorlevel 1 (
    echo ERROR: Maven compilation failed!
    pause
    exit /b 1
)

echo ✓ Compilation successful!
echo.

echo Starting data import...
echo.
call mvn exec:java -Dexec.mainClass="com.bookkeeping.util.DataImporter" -q

echo.
echo =================================================
echo Import process completed!
echo =================================================
pause