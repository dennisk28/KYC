package com.example.kycapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.kycapp.data.api.ApiClient
import com.example.kycapp.databinding.ActivityKycProcessBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*

class KycProcessActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityKycProcessBinding
    private var currentKycId: String? = null
    private var userId: String = UUID.randomUUID().toString()
    
    private val cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                // 根据当前操作类型显示图片
                if (binding.btnUploadIdCard.isEnabled) {
                    // 身份证上传
                    binding.ivIdCard.setImageBitmap(imageBitmap)
                    binding.ivIdCard.visibility = android.view.View.VISIBLE
                    uploadIdCard(imageBitmap)
                } else if (binding.btnUploadFace.isEnabled) {
                    // 人脸照片上传
                    binding.ivFacePhoto.setImageBitmap(imageBitmap)
                    binding.ivFacePhoto.visibility = android.view.View.VISIBLE
                    uploadFacePhoto(imageBitmap)
                }
            } else {
                Toast.makeText(this, "拍照失败，请重试", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        updateUI()
    }
    
    private fun setupViews() {
        binding.btnUploadIdCard.setOnClickListener {
            checkPermissionsAndOpenCamera()
        }
        
        binding.btnUploadFace.setOnClickListener {
            if (currentKycId != null) {
                checkPermissionsAndOpenCamera()
            } else {
                Toast.makeText(this, "请先上传身份证", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnCheckStatus.setOnClickListener {
            currentKycId?.let { kycId ->
                val intent = Intent(this, StatusActivity::class.java)
                intent.putExtra("kycId", kycId)
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "尚未开始KYC流程", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateUI() {
        binding.tvStep1.text = if (currentKycId != null) "✓ 身份证上传完成" else "1. 上传身份证"
        binding.tvStep2.text = if (binding.ivFacePhoto.drawable != null) "✓ 人脸照片上传完成" else "2. 上传人脸照片"
        binding.tvStep3.text = "3. 等待验证结果"
        
        binding.btnUploadIdCard.text = if (currentKycId != null) "身份证已上传" else "拍摄身份证"
        binding.btnUploadFace.text = if (binding.ivFacePhoto.drawable != null) "人脸照片已上传" else "拍摄人脸"
        
        binding.btnUploadIdCard.isEnabled = currentKycId == null
        binding.btnUploadFace.isEnabled = currentKycId != null && binding.ivFacePhoto.drawable == null
    }
    
    private fun checkPermissionsAndOpenCamera() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        openCamera()
                    } else {
                        Toast.makeText(this@KycProcessActivity, "需要相机和存储权限", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }
    
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        }
    }
    
    private fun uploadIdCard(bitmap: Bitmap) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = android.view.View.VISIBLE
                binding.tvStatus.text = "正在上传身份证..."
                
                val file = bitmapToFile(bitmap, "idcard_${System.currentTimeMillis()}.jpg")
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("idCardImage", file.name, requestFile)
                val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = ApiClient.kycApiService.uploadIdCard(userIdBody, body)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    currentKycId = response.body()?.data?.kycId
                    binding.tvStatus.text = "身份证上传成功，KYC ID: $currentKycId"
                    Toast.makeText(this@KycProcessActivity, "身份证上传成功", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvStatus.text = "身份证上传失败: ${response.body()?.message}"
                    Toast.makeText(this@KycProcessActivity, "上传失败", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                binding.tvStatus.text = "网络错误: ${e.message}"
                Toast.makeText(this@KycProcessActivity, "网络错误", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = android.view.View.GONE
                updateUI()
            }
        }
    }
    
    private fun uploadFacePhoto(bitmap: Bitmap) {
        currentKycId?.let { kycId ->
            lifecycleScope.launch {
                try {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                    binding.tvStatus.text = "正在上传人脸照片..."
                    
                    val file = bitmapToFile(bitmap, "face_${System.currentTimeMillis()}.jpg")
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("faceImage", file.name, requestFile)
                    
                    val response = ApiClient.kycApiService.uploadFace(kycId, body)
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        binding.tvStatus.text = "人脸照片上传成功，开始验证流程..."
                        Toast.makeText(this@KycProcessActivity, "上传成功，开始验证", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvStatus.text = "人脸照片上传失败: ${response.body()?.message}"
                        Toast.makeText(this@KycProcessActivity, "上传失败", Toast.LENGTH_SHORT).show()
                    }
                    
                } catch (e: Exception) {
                    binding.tvStatus.text = "网络错误: ${e.message}"
                    Toast.makeText(this@KycProcessActivity, "网络错误", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.progressBar.visibility = android.view.View.GONE
                    updateUI()
                }
            }
        }
    }
    
    private fun bitmapToFile(bitmap: Bitmap, filename: String): File {
        val file = File(cacheDir, filename)
        file.createNewFile()
        
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
        fileOutputStream.close()
        
        return file
    }
}