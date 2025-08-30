package com.example.kycapp.data.api

import com.example.kycapp.data.model.ApiResponse
import com.example.kycapp.data.model.KycStatusResponse
import com.example.kycapp.data.model.KycUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface KycApiService {
    
    @Multipart
    @POST("api/kyc/upload-id-card")
    suspend fun uploadIdCard(
        @Part("userId") userId: RequestBody,
        @Part idCardImage: MultipartBody.Part
    ): Response<ApiResponse<KycUploadResponse>>
    
    @Multipart
    @POST("api/kyc/{kycId}/upload-face")
    suspend fun uploadFace(
        @Path("kycId") kycId: String,
        @Part faceImage: MultipartBody.Part
    ): Response<ApiResponse<KycUploadResponse>>
    
    @GET("api/kyc/{kycId}/status")
    suspend fun getKycStatus(
        @Path("kycId") kycId: String
    ): Response<ApiResponse<KycStatusResponse>>
}