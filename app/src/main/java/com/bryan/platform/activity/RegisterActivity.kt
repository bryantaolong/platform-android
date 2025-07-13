// app/src/main/java/com/bryan/platform_android/activity/RegisterActivity.kt (新增文件)
package com.bryan.platform.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.databinding.ActivityRegisterBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.request.RegisterRequest
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.AuthService
import com.bryan.platform.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 注册界面 Activity
 * 负责处理用户注册的 UI 交互和网络请求
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authService: AuthService = RetrofitClient.createService(AuthService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置注册按钮的点击事件监听器
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // 设置返回登录文本的点击事件监听器
        binding.tvBackToLogin.setOnClickListener {
            finish() // 关闭当前注册页面，返回到上一个（登录）页面
        }
    }

    /**
     * 执行用户注册操作
     */
    private fun registerUser() {
        val username = binding.etRegisterUsername.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()

        // 简单的输入校验
        if (username.isEmpty()) {
            binding.etRegisterUsername.error = "用户名不能为空"
            binding.etRegisterUsername.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.etRegisterPassword.error = "密码不能为空"
            binding.etRegisterPassword.requestFocus()
            return
        }
        if (password.length < 6) { // 根据后端 @Size(min = 6) 校验
            binding.etRegisterPassword.error = "密码至少6位"
            binding.etRegisterPassword.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.etRegisterEmail.error = "邮箱不能为空"
            binding.etRegisterEmail.requestFocus()
            return
        }
        // 简单的邮箱格式校验，更严格的校验可以在后端进行
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.error = "邮箱格式不正确"
            binding.etRegisterEmail.requestFocus()
            return
        }

        // 创建 RegisterRequest 对象
        val registerRequest = RegisterRequest(username, password, email)

        // 调用 Retrofit 接口发起注册请求
        authService.register(registerRequest).enqueue(object : Callback<Result<User>> {
            override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        // 注册成功
                        val user = result.data
                        Toast.makeText(this@RegisterActivity, "注册成功！用户: ${user?.username}", Toast.LENGTH_LONG).show()
                        Log.d("RegisterActivity", "Registration successful: ${user?.username}")
                        // 注册成功后，可以返回登录页面，或者直接跳转到主页（带上新注册用户的token）
                        finish() // 返回登录页面
                    } else {
                        // 注册失败，显示后端返回的错误信息
                        val errorMessage = result?.message ?: "未知错误"
                        Toast.makeText(this@RegisterActivity, "注册失败: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("RegisterActivity", "Registration failed: ${result?.code} - $errorMessage")
                    }
                } else {
                    // HTTP 错误码
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@RegisterActivity, "注册请求失败: ${response.code()} - ${errorBody ?: "No error body"}", Toast.LENGTH_LONG).show()
                    Log.e("RegisterActivity", "HTTP Error: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                // 网络请求失败
                Toast.makeText(this@RegisterActivity, "网络请求失败: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("RegisterActivity", "Network request failed", t)
            }
        })
    }
}
