package com.bryan.platform.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.R
import com.bryan.platform.databinding.ActivityProfileBinding // Make sure this matches your layout file name
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.AuthService
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val authService: AuthService = RetrofitClient.createService(AuthService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable back button
        binding.toolbar.setNavigationOnClickListener { onBackPressed() } // Handle back button click

        fetchCurrentUser()
        setupBottomNavigation() // Setup bottom navigation

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun fetchCurrentUser() {
        binding.progressBarProfile.visibility = View.VISIBLE // Show loading indicator
        authService.getCurrentUser().enqueue(object : Callback<Result<User>> { // Corrected Result type
            override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                binding.progressBarProfile.visibility = View.GONE // Hide loading indicator
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val user = result.data
                        if (user != null) {
                            displayUserProfile(user)
                            Log.d("ProfileActivity", "Successfully fetched user profile: ${user.username}")
                        } else {
                            Toast.makeText(this@ProfileActivity, "用户数据为空", Toast.LENGTH_LONG).show()
                            Log.e("ProfileActivity", "API Error: User data is null")
                        }
                    } else {
                        val errorMsg = result?.message ?: "获取用户资料失败"
                        Toast.makeText(this@ProfileActivity, errorMsg, Toast.LENGTH_LONG).show()
                        Log.e("ProfileActivity", "API Error: $errorMsg")
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@ProfileActivity, errorMsg, Toast.LENGTH_LONG).show()
                    Log.e("ProfileActivity", errorMsg)
                }
            }

            override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                binding.progressBarProfile.visibility = View.GONE // Hide loading indicator
                val errorMsg = "网络请求失败: ${t.message}"
                Toast.makeText(this@ProfileActivity, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("ProfileActivity", errorMsg, t)
            }
        })
    }

    private fun displayUserProfile(user: User) {
        binding.tvUsername.text = "用户名: ${user.username}"
        binding.tvEmail.text = "邮箱: ${user.email}"
        binding.tvRoles.text = "角色: ${user.roles}"
        binding.tvStatus.text = "状态: ${if (user.status == 0) "正常" else "封禁"}"
        binding.tvCreateTime.text = "创建时间: ${user.createTime}"
        binding.tvUpdateTime.text = "更新时间: ${user.updateTime}"

        // You can add logic here to load a real profile picture if available
        // For now, it uses the default placeholder from XML
    }

    private fun logoutUser() {
        SessionManager.getInstance().clear() // Clear token from SessionManager
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show()

        // Navigate back to the login screen or main activity
        val intent = Intent(this, LoginActivity::class.java) // Assuming you have a LoginActivity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        finish() // Finish current activity
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id.navigation_home is the current activity, so no action needed or you can re-fetch data
                R.id.navigation_home -> {
                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
                    // This is also the current activity (Moments/Dashboard)
                    // No action needed, or you can ensure it's the top of the stack
                    true
                }
                R.id.navigation_profile -> {
                    // Optionally re-fetch moments or just stay on this screen
                    true
                }
                else -> false
            }
        }
        // Set the default selected item to match the current activity
        binding.bottomNavigation.selectedItemId = R.id.navigation_dashboard // Assuming MainActivity is the Dashboard/Moments screen
    }
}
