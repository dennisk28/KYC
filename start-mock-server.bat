@echo off
echo Starting Mock Verification Server...
echo.

echo Mock Server will run on port 9090
echo KYC Server should be running on port 8080
echo.

cd mock-server
mvn spring-boot:run