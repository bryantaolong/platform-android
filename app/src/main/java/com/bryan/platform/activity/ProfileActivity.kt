package com.bryan.platform.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.R // Import R for string and drawable resources
import com.bryan.platform.activity.auth.LoginActivity
import com.bryan.platform.activity.user.ChangePasswordActivity
import com.bryan.platform.activity.user.UserProfileEditActivity
import com.bryan.platform.databinding.ActivityProfileBinding
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

        setupBottomNavigation() // Setup bottom navigation for ProfileActivity

        fetchCurrentUser()

        // Setup click listeners for the new directly embedded options
        binding.optionUpdateUserInfo.setOnClickListener {
            Toast.makeText(this, "修改用户信息 clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Update User Info screen (e.g., new Activity for UserProfileEditActivity)
             val intent = Intent(this, UserProfileEditActivity::class.java)
             startActivity(intent)
        }

        binding.optionChangePassword.setOnClickListener {
            Toast.makeText(this, "修改密码 clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Change Password screen (e.g., new Activity for ChangePasswordActivity)
             val intent = Intent(this, ChangePasswordActivity::class.java)
             startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun fetchCurrentUser() {
        binding.progressBarProfile.visibility = View.VISIBLE // Show loading indicator
        authService.getCurrentUser().enqueue(object : Callback<Result<User>> {
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
                        Log.e("ProfileActivity", errorMsg)
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
        binding.tvUsername.text = user.username
        binding.tvUserId.text = "用户ID: ${user.id}" // Display user ID
        binding.tvBioDescription.text = getString(R.string.personal_profile_placeholder) // Placeholder for bio, update if you add a bio field to User model

        // The image doesn't show email, roles, status, createTime, updateTime directly in the main view.
        // If these are needed, they could be part of the "Account Settings" screen.
        // Removed direct display of email, roles, status, createTime, updateTime TextViews.
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
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java) // Assuming MainActivity is Home
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, MainActivity::class.java) // Assuming MainActivity is Dashboard
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Already on ProfileActivity, no action needed or re-fetch data
                    true
                }
                else -> false
            }
        }
        // Set the default selected item to match the current activity
        binding.bottomNavigation.selectedItemId = R.id.navigation_profile
    }
}
