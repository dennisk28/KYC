package com.example.kycapp.data.api

import com.example.kycapp.data.model.ApiResponse
import com.example.kycapp.data.model.CreateKycSessionRequest
import com.example.kycapp.data.model.CreateKycSessionResponse
import com.example.kycapp.data.model.KycStatusResponse
import com.example.kycapp.data.model.KycUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface KycApiService {

    @POST("api/kyc/session")
    suspend fun createKycSession(
        @Body body: CreateKycSessionRequest
    ): Response<ApiResponse<CreateKycSessionResponse>>

    @Multipart
    @POST("api/kyc/{kycId}/upload-id-card")
    suspend fun uploadIdCard(
        @Path("kycId") kycId: String,
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