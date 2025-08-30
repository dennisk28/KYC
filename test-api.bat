@echo off
echo Testing KYC Server APIs...

echo.
echo 1. Testing Health Check...
curl -s http://localhost:8080/actuator/health
echo.

echo.
echo 2. Testing Admin API - Get all KYC processes...
curl -s http://localhost:8080/api/admin/kyc
echo.

echo.
echo 3. To test file upload, use:
echo curl -X POST http://localhost:8080/api/kyc/upload-id-card -F "userId=test-user" -F "idCardImage=@test-image.jpg"
echo.

echo Server test completed!