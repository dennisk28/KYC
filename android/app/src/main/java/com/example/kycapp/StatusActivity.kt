package com.example.kycapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kycapp.data.api.ApiClient
import com.example.kycapp.databinding.ActivityStatusBinding
import kotlinx.coroutines.launch

class StatusActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStatusBinding
    private var kycId: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var statusCheckRunnable: Runnable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        kycId = intent.getStringExtra("kycId")
        
        setupViews()
        startPolling()
    }
    
    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            checkStatus()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun startPolling() {
        statusCheckRunnable = Runnable {
            checkStatus()
            handler.postDelayed(statusCheckRunnable!!, 3000) // 每3秒检查一次
        }
        handler.post(statusCheckRunnable!!)
    }
    
    private fun stopPolling() {
        statusCheckRunnable?.let {
            handler.removeCallbacks(it)
        }
    }
    
    private fun checkStatus() {
        kycId?.let { id ->
            lifecycleScope.launch {
                try {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                    
                    val response = ApiClient.kycApiService.getKycStatus(id)
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val statusData = response.body()?.data
                        updateStatusUI(statusData)
                    } else {
                        binding.tvStatus.text = "获取状态失败: ${response.body()?.message}"
                    }
                    
                } catch (e: Exception) {
                    binding.tvStatus.text = "网络错误: ${e.message}"
                } finally {
                    binding.progressBar.visibility = android.view.View.GONE
                }
            }
        }
    }
    
    private fun updateStatusUI(statusData: com.example.kycapp.data.model.KycStatusResponse?) {
        statusData?.let { data ->
            binding.tvKycId.text = "KYC ID: ${data.kycId}"
            binding.tvStatus.text = "状态: ${getStatusText(data.status)}"
            binding.progressBarStatus.progress = data.progress
            binding.tvProgress.text = "${data.progress}%"
            
            data.currentNode?.let {
                binding.tvCurrentNode.text = "当前节点: ${getNodeText(it)}"
                binding.tvCurrentNode.visibility = android.view.View.VISIBLE
            } ?: run {
                binding.tvCurrentNode.visibility = android.view.View.GONE
            }
            
            // 如果完成或失败，显示最终结果并停止轮询
            if (data.status == "COMPLETED" || data.status == "FAILED") {
                stopPolling()
                binding.btnRefresh.text = "刷新"
                
                data.result?.let { result ->
                    binding.tvResult.text = "验证结果: ${result}"
                    binding.tvResult.visibility = android.view.View.VISIBLE
                }
            } else {
                binding.tvResult.visibility = android.view.View.GONE
            }
        }
    }
    
    private fun getStatusText(status: String): String {
        return when (status) {
            "PENDING" -> "等待中"
            "IN_PROGRESS" -> "处理中"
            "COMPLETED" -> "已完成"
            "FAILED" -> "失败"
            else -> status
        }
    }
    
    private fun getNodeText(nodeName: String): String {
        return when (nodeName) {
            "Identity Document Verification" -> "身份证验证"
            "Face Verification and Comparison" -> "人脸验证和比对"
            "Deepfake Detection" -> "深度伪造检测"
            else -> nodeName
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopPolling()
    }
}