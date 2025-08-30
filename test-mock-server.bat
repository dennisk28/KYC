@echo off
echo Testing Mock Verification Server...
echo.

echo 1. Testing Mock Server Health...
curl -s http://localhost:9090/api/test/health
echo.
echo.

echo 2. Manual Callback Test - ID Verification (Pass)...
curl -X POST "http://localhost:9090/api/test/trigger-callback/test-task-123?type=id&passed=true"
echo.
echo.

echo 3. Manual Callback Test - Face Verification (Pass)...
curl -X POST "http://localhost:9090/api/test/trigger-callback/test-task-456?type=face&passed=true"
echo.
echo.

echo 4. Manual Callback Test - ID Verification (Fail)...
curl -X POST "http://localhost:9090/api/test/trigger-callback/test-task-789?type=id&passed=false"
echo.
echo.

echo 5. Test ID Card Verification API...
curl -X POST http://localhost:9090/api/verification/id-card ^
  -H "Content-Type: application/json" ^
  -d "{\"taskId\":\"test-task-001\",\"imageUrl\":\"http://example.com/id.jpg\",\"callbackUrl\":\"http://localhost:8080/api/callback/id-verification\"}"
echo.
echo.

echo 6. Test Face Verification API...
curl -X POST http://localhost:9090/api/verification/face ^
  -H "Content-Type: application/json" ^
  -d "{\"taskId\":\"test-task-002\",\"imageUrl\":\"http://example.com/face.jpg\",\"callbackUrl\":\"http://localhost:8080/api/callback/face-verification\"}"
echo.
echo.

echo Mock server test completed!
pause