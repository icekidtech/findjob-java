@echo off
REM FindJob Project Build Script for Windows
REM This script helps build and run the FindJob application

setlocal enabledelayedexpansion

REM Set colors
set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "RESET=[0m"

REM Check if Maven is installed
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%ERROR: Maven is not installed or not in PATH%RESET%
    echo Please install Maven 3.6+ from https://maven.apache.org/download.cgi
    exit /b 1
)

REM Display menu
echo.
echo %GREEN%======================================%RESET%
echo %GREEN%   FindJob Project Build Script%RESET%
echo %GREEN%======================================%RESET%
echo.
echo Choose an option:
echo 1. Clean and Build (skip tests)
echo 2. Clean and Build (with tests)
echo 3. Run application
echo 4. Run with H2 profile (in-memory database)
echo 5. Clear Maven cache
echo 6. Install dependencies only
echo 7. Exit
echo.

set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" (
    echo %YELLOW%Building project...%RESET%
    call mvn clean install -DskipTests
    if %errorlevel% equ 0 (
        echo %GREEN%Build successful!%RESET%
    ) else (
        echo %RED%Build failed!%RESET%
        exit /b 1
    )
) else if "%choice%"=="2" (
    echo %YELLOW%Building project with tests...%RESET%
    call mvn clean install
    if %errorlevel% equ 0 (
        echo %GREEN%Build successful!%RESET%
    ) else (
        echo %RED%Build failed!%RESET%
        exit /b 1
    )
) else if "%choice%"=="3" (
    echo %YELLOW%Running application...%RESET%
    call mvn spring-boot:run
) else if "%choice%"=="4" (
    echo %YELLOW%Running with H2 profile (development mode)...%RESET%
    echo Access the application at http://localhost:8080
    echo Access H2 console at http://localhost:8080/h2-console
    call mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
) else if "%choice%"=="5" (
    echo %YELLOW%Clearing Maven cache...%RESET%
    rmdir /s /q "%USERPROFILE%\.m2\repository" 2>nul
    echo %GREEN%Cache cleared successfully!%RESET%
) else if "%choice%"=="6" (
    echo %YELLOW%Installing dependencies...%RESET%
    call mvn dependency:resolve
    echo %GREEN%Dependencies installed!%RESET%
) else if "%choice%"=="7" (
    echo Exiting...
    exit /b 0
) else (
    echo %RED%Invalid choice!%RESET%
    exit /b 1
)

pause
endlocal
