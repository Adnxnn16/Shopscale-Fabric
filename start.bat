@echo off
setlocal

echo.
echo  ============================================================
echo   ShopScale Fabric — Full-Stack Microservices Platform
echo  ============================================================
echo.

:: Check Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo  [ERROR] Docker is not running. Please start Docker Desktop first.
    exit /b 1
)

echo  [1/2] Building and starting all 29 services...
echo        This takes ~3-5 minutes on first run (images cached after).
echo.

docker compose up --build -d

if errorlevel 1 (
    echo.
    echo  [ERROR] docker compose failed. Check output above.
    exit /b 1
)

echo.
echo  ============================================================
echo   SUCCESS — ShopScale Fabric is starting up!
echo  ============================================================
echo.
echo   Open these URLs in your browser:
echo.
echo   ** APP (start here) **
echo   http://localhost:3000        ^<-- Frontend Web App
echo.
echo   ** MONITORING **
echo   http://localhost:8761        ^<-- Eureka Service Registry
echo   http://localhost:9411        ^<-- Zipkin Distributed Tracing
echo   http://localhost:8080        ^<-- API Gateway
echo.
echo   ** NOTE: Allow 1-2 minutes for all services to become healthy **
echo   ** Run: docker compose ps    to check service health status   **
echo  ============================================================
echo.

endlocal
