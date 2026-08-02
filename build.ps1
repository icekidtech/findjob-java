# FindJob Project Build Script for Windows PowerShell
# This script helps build and run the FindJob application

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("clean-build", "build-with-tests", "run", "run-h2", "clear-cache", "install-deps")]
    [string]$Action
)

# Color functions
function Write-Success {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Red
}

# Check if Maven is installed
function Test-MavenInstalled {
    $mvnPath = Get-Command mvn -ErrorAction SilentlyContinue
    if ($null -eq $mvnPath) {
        Write-Error "ERROR: Maven is not installed or not in PATH"
        Write-Warning "Please install Maven 3.6+ from https://maven.apache.org/download.cgi"
        exit 1
    }
    Write-Success "Maven found: $(mvn --version | Select-Object -First 1)"
}

# Show menu if no action specified
function Show-Menu {
    Write-Host ""
    Write-Host "======================================" -ForegroundColor Green
    Write-Host "   FindJob Project Build Script" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage: .\build.ps1 -Action <action>"
    Write-Host ""
    Write-Host "Available actions:"
    Write-Host "  clean-build      - Clean and build (skip tests)"
    Write-Host "  build-with-tests - Clean and build (with tests)"
    Write-Host "  run              - Run application (PostgreSQL)"
    Write-Host "  run-h2           - Run with H2 (in-memory database)"
    Write-Host "  clear-cache      - Clear Maven cache"
    Write-Host "  install-deps     - Install dependencies only"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\build.ps1 -Action clean-build"
    Write-Host "  .\build.ps1 -Action run-h2"
}

# Build actions
function Invoke-CleanBuild {
    Write-Warning "Building project..."
    & mvn clean install -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Build successful!"
    } else {
        Write-Error "Build failed!"
        exit 1
    }
}

function Invoke-BuildWithTests {
    Write-Warning "Building project with tests..."
    & mvn clean install
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Build successful!"
    } else {
        Write-Error "Build failed!"
        exit 1
    }
}

function Invoke-Run {
    Write-Warning "Running application (PostgreSQL)..."
    Write-Host "Access the application at http://localhost:8080" -ForegroundColor Cyan
    & mvn spring-boot:run
}

function Invoke-RunH2 {
    Write-Warning "Running with H2 profile (development mode)..."
    Write-Host "Access the application at http://localhost:8080" -ForegroundColor Cyan
    Write-Host "Access H2 console at http://localhost:8080/h2-console" -ForegroundColor Cyan
    & mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
}

function Invoke-ClearCache {
    Write-Warning "Clearing Maven cache..."
    $cacheDir = "$env:USERPROFILE\.m2\repository"
    if (Test-Path $cacheDir) {
        Remove-Item $cacheDir -Recurse -Force -ErrorAction SilentlyContinue
        Write-Success "Cache cleared successfully!"
    } else {
        Write-Host "Cache directory not found"
    }
}

function Invoke-InstallDeps {
    Write-Warning "Installing dependencies..."
    & mvn dependency:resolve
    Write-Success "Dependencies installed!"
}

# Main execution
Test-MavenInstalled

if ([string]::IsNullOrEmpty($Action)) {
    Show-Menu
} else {
    switch ($Action) {
        "clean-build" { Invoke-CleanBuild }
        "build-with-tests" { Invoke-BuildWithTests }
        "run" { Invoke-Run }
        "run-h2" { Invoke-RunH2 }
        "clear-cache" { Invoke-ClearCache }
        "install-deps" { Invoke-InstallDeps }
        default { Show-Menu }
    }
}
