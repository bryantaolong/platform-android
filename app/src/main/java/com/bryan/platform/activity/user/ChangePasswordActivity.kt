package com.bryan.platform.activity.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.databinding.ActivityChangePasswordBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.request.ChangePasswordRequest
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.AuthService
import com.bryan.platform.network.UserService
import com.bryan.platform.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val authService: AuthService = RetrofitClient.createService(AuthService::class.java)
    private val userService: UserService = RetrofitClient.createService(UserService::class.java)

    private var currentUserId: Long? = null // 用于存储当前用户ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 启用返回按钮
        binding.toolbar.setNavigationOnClickListener { onBackPressed() } // 处理返回按钮点击事件

        fetchCurrentUserId() // 获取用户ID以用于密码修改API

        binding.btnChangePassword.setOnClickListener {
            changePassword() // 修改密码
        }
    }

    /**
     * 获取当前用户的 ID。
     * 在实际应用中，用户 ID 可能通过 SessionManager 或 Intent 传递。
     * 此处为简化实现，通过再次调用 getCurrentUser 获取。
     */
    private fun fetchCurrentUserId() {
        binding.progressBarChangePassword.visibility = View.VISIBLE // 显示加载指示器
        authService.getCurrentUser().enqueue(object : Callback<Result<User>> {
            override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                binding.progressBarChangePassword.visibility = View.GONE // 隐藏加载指示器
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        currentUserId = result.data?.id
                        if (currentUserId == null) {
                            Toast.makeText(this@ChangePasswordActivity, "无法获取用户ID", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        val errorMsg = result?.message ?: "获取用户ID失败"
                        Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                        finish()
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                binding.progressBarChangePassword.visibility = View.GONE // 隐藏加载指示器
                val errorMsg = "网络请求失败: ${t.message}"
                Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    /**
     * 收集用户输入的密码，并调用 UserService 修改密码。
     */
    private fun changePassword() {
        val oldPassword = binding.etOldPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmNewPassword = binding.etConfirmNewPassword.text.toString()

        if (oldPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
            Toast.makeText(this, "所有密码字段都不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmNewPassword) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUserId == null) {
            Toast.makeText(this, "用户ID未加载，无法修改密码", Toast.LENGTH_SHORT).show()
            return
        }

        val changePasswordRequest = ChangePasswordRequest(oldPassword, newPassword)

        binding.progressBarChangePassword.visibility = View.VISIBLE // 显示加载指示器
        currentUserId?.let { userId -> // 确保用户 ID 不为空
            userService.changePassword(userId, changePasswordRequest).enqueue(object : Callback<Result<User>> {
                override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                    binding.progressBarChangePassword.visibility = View.GONE // 隐藏加载指示器
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess()) {
                            Toast.makeText(this@ChangePasswordActivity, "密码修改成功", Toast.LENGTH_SHORT).show()
                            // 密码修改成功后，可以清空密码字段或返回上一个界面
                            finish()
                        } else {
                            val errorMsg = result?.message ?: "密码修改失败"
                            Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorMsg = "HTTP Error: ${response.code()}"
                        Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                    binding.progressBarChangePassword.visibility = View.GONE // 隐藏加载指示器
                    val errorMsg = "网络请求失败: ${t.message}"
                    Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
