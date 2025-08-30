@echo off
echo Testing Complete KYC Flow with Mock Server...
echo.

echo Prerequisite: 
echo - KYC Server running on port 8080
echo - Mock Server running on port 9090
echo.

echo 1. Creating KYC Session...
for /f "tokens=*" %%a in ('curl -s -X POST http://localhost:8080/api/kyc/session -H "Content-Type: application/json" -d "{\"userId\":\"test-user-456\"}"') do set session_response=%%a
echo Session Response: %session_response%
echo.

echo 2. Please extract kycId from above response and use it in the following commands:
echo.

echo 3. Upload ID Card (replace YOUR_KYC_ID):
echo curl -X POST http://localhost:8080/api/kyc/YOUR_KYC_ID/upload-id-card -F "idCardImage=@test-image.jpg"
echo.

echo 4. Upload Face Image (replace YOUR_KYC_ID):
echo curl -X POST http://localhost:8080/api/kyc/YOUR_KYC_ID/upload-face -F "faceImage=@face-image.jpg"
echo.

echo 5. Check Status (replace YOUR_KYC_ID):
echo curl http://localhost:8080/api/kyc/YOUR_KYC_ID/status
echo.

echo 6. Manual Callback Trigger (if needed):
echo curl -X POST "http://localhost:9090/api/test/trigger-callback/YOUR_TASK_ID?type=id&passed=true"
echo curl -X POST "http://localhost:9090/api/test/trigger-callback/YOUR_TASK_ID?type=face&passed=true"
echo.

pause