# KYC System API Documentation

## Overview
KYC (Know Your Customer) 身份验证系统 API 接口文档。系统提供身份证验证和人脸识别服务。

## Base URLs
- **Backend API**: `http://localhost:8080`
- **Mock Server**: `http://localhost:8081`

---

## Client APIs (Android)

### 1. Upload ID Card
上传身份证照片并创建 KYC 会话

**Endpoint:** `POST /api/kyc/upload-id-card`

**Request:**
```
Content-Type: multipart/form-data

Parameters:
- userId (string): 用户唯一标识符
- idCardImage (file): 身份证图片文件 (JPEG/PNG)
```

**Response:**
```json
{
  "success": true,
  "message": "身份证上传成功",
  "data": {
    "kycId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ID_VERIFICATION_PENDING"
  }
}
```

### 2. Upload Face Photo
上传人脸照片

**Endpoint:** `POST /api/kyc/{kycId}/upload-face`

**Request:**
```
Content-Type: multipart/form-data

Path Parameters:
- kycId (string): KYC 会话 ID

Parameters:
- faceImage (file): 人脸照片文件 (JPEG/PNG)
```

**Response:**
```json
{
  "success": true,
  "message": "人脸照片上传成功",
  "data": {
    "kycId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "FACE_VERIFICATION_PENDING"
  }
}
```

### 3. Get KYC Status
查询 KYC 验证状态

**Endpoint:** `GET /api/kyc/{kycId}/status`

**Request:**
```
Path Parameters:
- kycId (string): KYC 会话 ID
```

**Response:**
```json
{
  "success": true,
  "data": {
    "kycId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user123",
    "status": "COMPLETED",
    "currentNode": "FACE_VERIFICATION",
    "progress": 100,
    "result": "APPROVED",
    "createdAt": "2025-08-30T12:00:00Z",
    "updatedAt": "2025-08-30T12:05:30Z",
    "workflow": {
      "nodes": [
        {
          "type": "ID_VERIFICATION",
          "status": "COMPLETED",
          "result": "APPROVED"
        },
        {
          "type": "FACE_VERIFICATION", 
          "status": "COMPLETED",
          "result": "APPROVED"
        }
      ]
    }
  }
}
```

---

## Admin APIs (Web Console)

### 1. List All KYC Processes
获取所有 KYC 进程列表

**Endpoint:** `GET /api/admin/kyc`

**Request:**
```
Query Parameters:
- page (int, optional): 页码，默认 0
- size (int, optional): 每页大小，默认 20
- status (string, optional): 状态过滤 (PENDING, IN_PROGRESS, COMPLETED, FAILED)
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "kycId": "550e8400-e29b-41d4-a716-446655440000",
        "userId": "user123",
        "status": "COMPLETED",
        "result": "APPROVED",
        "createdAt": "2025-08-30T12:00:00Z",
        "updatedAt": "2025-08-30T12:05:30Z"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 20,
    "number": 0
  }
}
```

### 2. Get KYC Process Details
获取特定 KYC 进程的详细信息

**Endpoint:** `GET /api/admin/kyc/{kycId}`

**Request:**
```
Path Parameters:
- kycId (string): KYC 会话 ID
```

**Response:**
```json
{
  "success": true,
  "data": {
    "kycId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user123",
    "status": "COMPLETED",
    "currentNode": "FACE_VERIFICATION",
    "progress": 100,
    "result": "APPROVED",
    "createdAt": "2025-08-30T12:00:00Z",
    "updatedAt": "2025-08-30T12:05:30Z",
    "workflow": {
      "configId": "default-workflow",
      "nodes": [
        {
          "type": "ID_VERIFICATION",
          "status": "COMPLETED",
          "result": "APPROVED",
          "startedAt": "2025-08-30T12:00:30Z",
          "completedAt": "2025-08-30T12:02:15Z"
        },
        {
          "type": "FACE_VERIFICATION",
          "status": "COMPLETED", 
          "result": "APPROVED",
          "startedAt": "2025-08-30T12:02:20Z",
          "completedAt": "2025-08-30T12:05:30Z"
        }
      ]
    },
    "files": {
      "idCardPath": "/uploads/550e8400-e29b-41d4-a716-446655440000_id.jpg",
      "facePath": "/uploads/550e8400-e29b-41d4-a716-446655440000_face.jpg"
    }
  }
}
```

### 3. Delete KYC Process
删除 KYC 进程

**Endpoint:** `DELETE /api/admin/kyc/{kycId}`

**Request:**
```
Path Parameters:
- kycId (string): KYC 会话 ID
```

**Response:**
```json
{
  "success": true,
  "message": "KYC进程删除成功"
}
```

### 4. Get System Statistics
获取系统统计信息

**Endpoint:** `GET /api/admin/statistics`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalProcesses": 150,
    "completedProcesses": 120,
    "pendingProcesses": 20,
    "failedProcesses": 10,
    "todayProcesses": 25,
    "approvalRate": 0.85
  }
}
```

---

## Third-party Callback APIs

### 1. ID Verification Callback
身份证验证结果回调

**Endpoint:** `POST /api/callback/id-verification`

**Request:**
```json
{
  "kycId": "550e8400-e29b-41d4-a716-446655440000",
  "result": "APPROVED",
  "confidence": 0.95,
  "details": {
    "documentType": "ID_CARD",
    "extractedInfo": {
      "name": "张三",
      "idNumber": "11010119900101****",
      "validUntil": "2030-12-31"
    }
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "回调处理成功"
}
```

### 2. Face Verification Callback
人脸验证结果回调

**Endpoint:** `POST /api/callback/face-verification`

**Request:**
```json
{
  "kycId": "550e8400-e29b-41d4-a716-446655440000",
  "result": "APPROVED",
  "confidence": 0.92,
  "details": {
    "faceMatch": true,
    "livenessDetection": true,
    "qualityScore": 0.88
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "回调处理成功"
}
```

---

## Status Codes

### KYC Process Status
- `PENDING`: 等待开始
- `IN_PROGRESS`: 验证进行中
- `COMPLETED`: 验证完成
- `FAILED`: 验证失败

### Node Status
- `PENDING`: 节点等待执行
- `IN_PROGRESS`: 节点执行中
- `COMPLETED`: 节点执行完成
- `FAILED`: 节点执行失败

### Verification Results
- `APPROVED`: 验证通过
- `REJECTED`: 验证拒绝
- `PENDING`: 等待结果

---

## Error Responses

所有 API 在出错时返回统一格式：

```json
{
  "success": false,
  "message": "错误描述",
  "error": "ERROR_CODE"
}
```

### Common Error Codes
- `INVALID_REQUEST`: 请求参数无效
- `KYC_NOT_FOUND`: KYC 会话不存在
- `FILE_UPLOAD_ERROR`: 文件上传失败
- `WORKFLOW_ERROR`: 工作流执行错误
- `THIRD_PARTY_ERROR`: 第三方服务错误

---

## Development Notes

### Authentication
当前版本未实现身份认证，生产环境需要添加 JWT 或 OAuth2 认证。

### File Upload Limits
- 最大文件大小: 10MB
- 支持格式: JPEG, PNG
- 文件存储路径: `backend/uploads/`

### Rate Limiting
建议在生产环境添加 API 速率限制。

### CORS Configuration
已配置允许前端跨域访问，生产环境需要限制允许的域名。