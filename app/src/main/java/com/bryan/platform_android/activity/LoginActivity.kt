// app/src/main/java/com/bryan/platform_android/activity/LoginActivity.kt
package com.bryan.platform_android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform_android.MainActivity // 导入 MainActivity
import com.bryan.platform_android.databinding.ActivityLoginBinding
import com.bryan.platform_android.model.request.LoginRequest
import com.bryan.platform_android.model.User
import com.bryan.platform_android.model.response.Result
import com.bryan.platform_android.network.AuthService
import com.bryan.platform_android.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 登录界面 Activity
 * 负责处理用户登录的 UI 交互和网络请求
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authService: AuthService = RetrofitClient.createService(AuthService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        // 设置注册文本的点击事件监听器
        binding.tvRegister.setOnClickListener {
            // 跳转到注册页面
            startActivity(Intent(this, RegisterActivity::class.java)) // 启动 RegisterActivity
        }
    }

    /**
     * 执行用户登录操作
     */
    private fun loginUser() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty()) {
            binding.etUsername.error = "用户名不能为空"
            binding.etUsername.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "密码不能为空"
            binding.etPassword.requestFocus()
            return
        }

        val loginRequest = LoginRequest(username, password)

        authService.login(loginRequest).enqueue(object : Callback<Result<String>> {
            override fun onResponse(call: Call<Result<String>>, response: Response<Result<String>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val token = result.data
                        Toast.makeText(this@LoginActivity, "登录成功！Token: $token", Toast.LENGTH_LONG).show()
                        Log.d("LoginActivity", "Login successful, Token: $token")
                        // TODO: 将 Token 保存到 SharedPreferences 或其他安全存储中

                        // ==== 新增跳转到 MainActivity 的代码 ====
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        // 清除所有之前的 Activity，确保 MainActivity 成为新的任务栈根
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // 关闭登录页面，防止返回到登录界面
                        // ======================================

                    } else {
                        val errorMessage = result?.message ?: "未知错误"
                        Toast.makeText(this@LoginActivity, "登录失败: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("LoginActivity", "Login failed: ${result?.code} - $errorMessage")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@LoginActivity, "登录请求失败: ${response.code()} - ${errorBody ?: "No error body"}", Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "HTTP Error: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<Result<String>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "网络请求失败: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "Network request failed", t)
            }
        })
    }
}
