package com.bryan.platform.activity.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.databinding.ActivityUserProfileEditBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.request.UserUpdateRequest
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.AuthService
import com.bryan.platform.network.UserService
import com.bryan.platform.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileEditBinding
    private val authService: AuthService = RetrofitClient.createService(AuthService::class.java)
    private val userService: UserService = RetrofitClient.createService(UserService::class.java)

    private var currentUser: User? = null // 用于存储当前用户数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 启用返回按钮
        binding.toolbar.setNavigationOnClickListener { onBackPressed() } // 处理返回按钮点击事件

        fetchCurrentUserForEdit() // 获取当前用户信息以填充表单

        binding.btnSaveProfile.setOnClickListener {
            saveUserProfile() // 保存用户资料
        }
    }

    /**
     * 从后端获取当前用户的信息，并填充到编辑字段中。
     */
    private fun fetchCurrentUserForEdit() {
        binding.progressBarEditProfile.visibility = View.VISIBLE // 显示加载指示器
        authService.getCurrentUser().enqueue(object : Callback<Result<User>> {
            override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                binding.progressBarEditProfile.visibility = View.GONE // 隐藏加载指示器
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        currentUser = result.data
                        currentUser?.let { user ->
                            binding.etUsername.setText(user.username)
                            binding.etEmail.setText(user.email)
                        } ?: run {
                            Toast.makeText(this@UserProfileEditActivity, "无法获取用户数据", Toast.LENGTH_LONG).show()
                            finish() // 如果用户数据为空，关闭当前 Activity
                        }
                    } else {
                        val errorMsg = result?.message ?: "获取用户资料失败"
                        Toast.makeText(this@UserProfileEditActivity, errorMsg, Toast.LENGTH_LONG).show()
                        finish() // API 错误时关闭 Activity
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@UserProfileEditActivity, errorMsg, Toast.LENGTH_LONG).show()
                    finish() // HTTP 错误时关闭 Activity
                }
            }

            override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                binding.progressBarEditProfile.visibility = View.GONE // 隐藏加载指示器
                val errorMsg = "网络请求失败: ${t.message}"
                Toast.makeText(this@UserProfileEditActivity, errorMsg, Toast.LENGTH_LONG).show()
                finish() // 网络失败时关闭 Activity
            }
        })
    }

    /**
     * 收集用户输入，并调用 UserService 更新用户信息。
     */
    private fun saveUserProfile() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (currentUser == null) {
            Toast.makeText(this, "用户数据未加载，无法保存", Toast.LENGTH_SHORT).show()
            return
        }

        // 只有当字段发生变化时才包含在更新请求中
        val updatedUsername = if (username != currentUser?.username) username else null
        val updatedEmail = if (email != currentUser?.email) email else null

        // 如果没有检测到任何更改，则提示用户并返回
        if (updatedUsername == null && updatedEmail == null) {
            Toast.makeText(this, "没有检测到信息更改", Toast.LENGTH_SHORT).show()
            return
        }

        val userUpdateRequest = UserUpdateRequest(updatedUsername, updatedEmail)

        binding.progressBarEditProfile.visibility = View.VISIBLE // 显示加载指示器
        currentUser?.id?.let { userId -> // 确保用户 ID 不为空
            userService.updateUser(userId, userUpdateRequest).enqueue(object : Callback<Result<User>> {
                override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                    binding.progressBarEditProfile.visibility = View.GONE // 隐藏加载指示器
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess()) {
                            Toast.makeText(this@UserProfileEditActivity, "用户信息更新成功", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK) // 设置结果为成功，通知调用方
                            finish() // 关闭当前 Activity
                        } else {
                            val errorMsg = result?.message ?: "用户信息更新失败"
                            Toast.makeText(this@UserProfileEditActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorMsg = "HTTP Error: ${response.code()}"
                        Toast.makeText(this@UserProfileEditActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                    binding.progressBarEditProfile.visibility = View.GONE // 隐藏加载指示器
                    val errorMsg = "网络请求失败: ${t.message}"
                    Toast.makeText(this@UserProfileEditActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "用户ID无效，无法更新", Toast.LENGTH_SHORT).show()
        }
    }
}
