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
del bookkeeping.db 2>nul
echo Database file deleted and will be recreated on next app start.

echo.
echo Database cleared!
pause
