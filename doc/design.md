# KYC系统设计文档

## 系统架构

### 整体架构
```
Android客户端 -> Spring Boot后端 -> MongoDB数据库
                    ↓
             第三方证件检测服务
                    ↓  
             第三方人脸检测服务
                    ↓
            React管理控制台
```

### 核心组件

#### 1. Android客户端 (Kotlin)
- 证件照片采集
- 人脸照片采集
- 文件上传
- 结果轮询/推送通知

#### 2. Spring Boot后端服务
- RESTful API接口
- 工作流引擎
- 异步任务处理
- 第三方服务集成
- 回调处理

#### 3. MongoDB数据库
- KYC流程状态存储
- 文件元数据存储
- 工作流配置存储

#### 4. React管理控制台
- KYC流程监控
- 状态查看
- 文件管理
- 流程管理

## 数据模型

### KYC流程实体
```json
{
  "_id": "ObjectId",
  "userId": "String",
  "status": "PENDING/IN_PROGRESS/COMPLETED/FAILED",
  "createdTime": "Date",
  "updatedTime": "Date",
  "idCardInfo": {
    "fileName": "String",
    "filePath": "String",
    "uploadTime": "Date",
    "verificationStatus": "PENDING/SUCCESS/FAILED",
    "verificationResult": "Object"
  },
  "faceInfo": {
    "fileName": "String", 
    "filePath": "String",
    "uploadTime": "Date",
    "verificationStatus": "PENDING/SUCCESS/FAILED",
    "verificationResult": "Object"
  },
  "workflowNodes": [
    {
      "nodeId": "String",
      "nodeName": "String",
      "nodeType": "ID_VERIFICATION/FACE_VERIFICATION/DEEPFAKE_DETECTION",
      "status": "PENDING/IN_PROGRESS/COMPLETED/FAILED",
      "startTime": "Date",
      "endTime": "Date",
      "result": "Object"
    }
  ],
  "finalResult": {
    "passed": "Boolean",
    "reason": "String",
    "confidence": "Double"
  }
}
```

### 工作流配置实体
```json
{
  "_id": "ObjectId",
  "workflowName": "String",
  "version": "String",
  "nodes": [
    {
      "nodeId": "String",
      "nodeName": "String", 
      "nodeType": "String",
      "serviceEndpoint": "String",
      "requiredInputs": ["String"],
      "nextNodes": ["String"],
      "timeout": "Integer"
    }
  ],
  "isActive": "Boolean"
}
```

## API设计

### Android客户端API

#### 1. 上传证件照片
```
POST /api/kyc/upload-id-card
Content-Type: multipart/form-data

Parameters:
- userId: String
- idCardImage: File

Response:
{
  "success": true,
  "data": {
    "kycId": "String",
    "uploadId": "String"
  }
}
```

#### 2. 上传人脸照片
```
POST /api/kyc/{kycId}/upload-face
Content-Type: multipart/form-data

Parameters:
- faceImage: File

Response:
{
  "success": true,
  "data": {
    "uploadId": "String"
  }
}
```

#### 3. 查询KYC状态
```
GET /api/kyc/{kycId}/status

Response:
{
  "success": true,
  "data": {
    "kycId": "String",
    "status": "String",
    "progress": "Integer",
    "currentNode": "String",
    "result": "Object"
  }
}
```

### 管理控制台API

#### 1. 获取KYC列表
```
GET /api/admin/kyc?page=0&size=20&status=ALL

Response:
{
  "success": true,
  "data": {
    "content": [KycProcess],
    "totalElements": 100,
    "totalPages": 5
  }
}
```

#### 2. 获取KYC详情
```
GET /api/admin/kyc/{kycId}

Response:
{
  "success": true,
  "data": KycProcess
}
```

#### 3. 删除KYC流程
```
DELETE /api/admin/kyc/{kycId}
```

### 第三方服务回调API

#### 1. 证件验证回调
```
POST /api/callback/id-verification
{
  "taskId": "String",
  "status": "SUCCESS/FAILED",
  "result": {
    "isValid": "Boolean",
    "confidence": "Double",
    "details": "Object"
  }
}
```

#### 2. 人脸验证回调
```
POST /api/callback/face-verification
{
  "taskId": "String", 
  "status": "SUCCESS/FAILED",
  "result": {
    "isValid": "Boolean",
    "similarity": "Double",
    "confidence": "Double"
  }
}
```

## 工作流设计

### 默认KYC工作流
1. **证件验证节点**
   - 调用第三方证件检测服务
   - 异步等待验证结果
   
2. **人脸检测节点**
   - 调用第三方人脸检测服务
   - 进行活体检测
   - 与证件照片进行人脸比对
   
3. **结果汇总节点**
   - 综合各节点结果
   - 生成最终KYC结论

### 可扩展节点
- **深度伪造检测节点**
- **OCR文字识别节点**
- **风险评估节点**

## 部署架构

### 开发环境
- Spring Boot应用 (端口: 8080)
- MongoDB (端口: 27017)
- React开发服务器 (端口: 3000)

### 生产环境
- 负载均衡器
- 多实例Spring Boot应用
- MongoDB副本集
- 文件存储服务
- 日志收集系统

## 技术实现要点

### 异步处理
- 使用Spring的@Async注解
- CompletableFuture处理异步结果
- 消息队列缓冲第三方回调

### 文件管理
- 分布式文件存储
- 图片压缩和格式转换
- 访问权限控制

### 监控告警
- 应用性能监控
- 异常告警机制
- 业务指标统计

### 安全措施
- API访问权限控制
- 文件访问加密
- 敏感数据脱敏
- 审计日志记录