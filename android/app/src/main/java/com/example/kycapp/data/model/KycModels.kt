package com.example.kycapp.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
    val errorCode: String?
)

data class KycUploadResponse(
    val kycId: String,
    val uploadId: String
)

data class KycStatusResponse(
    val kycId: String,
    val status: String,
    val progress: Int,
    val currentNode: String?,
    val result: Any?
)

data class KycProcess(
    val id: String,
    val userId: String,
    val status: String,
    val createdTime: String,
    val updatedTime: String,
    val idCardInfo: IdCardInfo?,
    val faceInfo: FaceInfo?,
    val finalResult: FinalResult?
)

data class IdCardInfo(
    val fileName: String,
    val filePath: String,
    val uploadTime: String,
    val verificationStatus: String
)

data class FaceInfo(
    val fileName: String,
    val filePath: String,
    val uploadTime: String,
    val verificationStatus: String
)

data class FinalResult(
    val passed: Boolean,
    val reason: String,
    val confidence: Double
)