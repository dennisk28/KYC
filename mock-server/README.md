# Mock Verification Server

模拟第三方验证服务的Mock服务器，用于测试KYC系统的回调功能。

## 启动服务器

```bash
# 启动Mock服务器（端口9090）
start-mock-server.bat

# 或手动启动
cd mock-server
mvn spring-boot:run
```

## API接口

### 验证服务接口

#### 身份证验证
```
POST http://localhost:9090/api/verification/id-card
Content-Type: application/json

{
    "taskId": "任务ID",
    "imageUrl": "图片URL",
    "callbackUrl": "http://localhost:8080/api/callback/id-verification"
}
```

#### 人脸验证
```
POST http://localhost:9090/api/verification/face
Content-Type: application/json

{
    "taskId": "任务ID", 
    "imageUrl": "图片URL",
    "callbackUrl": "http://localhost:8080/api/callback/face-verification"
}
```

#### 深度伪造检测
```
POST http://localhost:9090/api/verification/deepfake
Content-Type: application/json

{
    "taskId": "任务ID",
    "imageUrl": "图片URL", 
    "callbackUrl": "http://localhost:8080/api/callback/deepfake-detection"
}
```

### 测试接口

#### 手动触发回调
```
POST http://localhost:9090/api/test/trigger-callback/{taskId}?type={id|face|deepfake}&passed={true|false}
```

#### 健康检查
```
GET http://localhost:9090/api/test/health
```

## 使用流程

1. **启动两个服务器**：
   - KYC服务器：`mvn spring-boot:run`（端口8080）
   - Mock服务器：`start-mock-server.bat`（端口9090）

2. **测试完整流程**：
   ```bash
   # 1. 创建KYC会话
   curl -X POST http://localhost:8080/api/kyc/session \
     -H "Content-Type: application/json" \
     -d '{"userId":"test-user"}'
   
   # 2. 上传身份证（使用返回的kycId）
   curl -X POST http://localhost:8080/api/kyc/{kycId}/upload-id-card \
     -F "idCardImage=@test-image.jpg"
   
   # 3. 上传人脸照片（自动触发工作流）
   curl -X POST http://localhost:8080/api/kyc/{kycId}/upload-face \
     -F "faceImage=@face-image.jpg"
   
   # 4. Mock服务器会自动发送回调到KYC服务器
   # 5. 查询状态看结果
   curl http://localhost:8080/api/kyc/{kycId}/status
   ```

3. **手动触发回调测试**：
   ```bash
   # 触发身份证验证通过
   curl -X POST "http://localhost:9090/api/test/trigger-callback/your-task-id?type=id&passed=true"
   
   # 触发人脸验证失败
   curl -X POST "http://localhost:9090/api/test/trigger-callback/your-task-id?type=face&passed=false"
   ```

## 回调行为

- **延迟模拟**：3-8秒随机延迟模拟真实处理时间
- **结果随机化**：大部分验证会通过，少数会失败
- **置信度**：随机生成70%-100%的置信度
- **自动回调**：验证完成后自动调用KYC服务器的回调接口