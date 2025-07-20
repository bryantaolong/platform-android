package com.bryan.platform.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.R // Import R for string and drawable resources
import com.bryan.platform.activity.MainActivity
import com.bryan.platform.activity.auth.LoginActivity
import com.bryan.platform.databinding.ActivityProfileBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.MyBatisPlusPage // Corrected import for Page
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.AuthService
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.network.UserFollowService // Import UserFollowService
import com.bryan.platform.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val authService: AuthService = RetrofitClient.createService(AuthService::class.java)
    private val userFollowService: UserFollowService = RetrofitClient.createService(UserFollowService::class.java) // Initialize UserFollowService

    private var currentUserId: Long? = null // Store current user ID for follow/follower counts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable back button
        binding.toolbar.setNavigationOnClickListener { this.onBackPressed() } // Handle back button click

        setupBottomNavigation() // Setup bottom navigation for ProfileActivity

        fetchCurrentUser()

        // Setup click listeners for the new directly embedded options
        binding.optionUpdateUserInfo.setOnClickListener {
            Toast.makeText(this, "修改用户信息 clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserProfileEditActivity::class.java)
            startActivity(intent)
        }

        binding.optionChangePassword.setOnClickListener {
            Toast.makeText(this, "修改密码 clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Setup click listeners for Following and Followers counts
        binding.llFollowingCountClickable.setOnClickListener {
            currentUserId?.let { id ->
                val intent = Intent(this, FollowingListActivity::class.java)
                intent.putExtra("userId", id) // Pass the user ID to the list activity
                startActivity(intent)
            } ?: Toast.makeText(this, "用户ID未加载，无法查看关注列表", Toast.LENGTH_SHORT).show()
        }

        binding.llFollowersCountClickable.setOnClickListener {
            currentUserId?.let { id ->
                val intent = Intent(this, FollowersListActivity::class.java)
                intent.putExtra("userId", id) // Pass the user ID to the list activity
                startActivity(intent)
            } ?: Toast.makeText(this, "用户ID未加载，无法查看粉丝列表", Toast.LENGTH_SHORT).show()
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
                            currentUserId = user.id // Store current user ID from API response
                            displayUserProfile(user) // Pass the user object to display
                            fetchFollowCounts(user.id) // Fetch follow counts using the ID from API
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

    @SuppressLint("SetTextI18n")
    private fun displayUserProfile(user: User) {
        // Prioritize username from SessionManager, fallback to API response
        binding.tvUsername.text = SessionManager.getInstance().fetchUsername() ?: user.username
        binding.tvBioDescription.text = getString(R.string.personal_profile_placeholder) // Placeholder for bio
    }

    private fun fetchFollowCounts(userId: Long) {
        // Fetch Following Count
        userFollowService.getFollowingUsers(userId, pageNum = 1, pageSize = 1).enqueue(object : Callback<Result<MyBatisPlusPage<User>>> {
            override fun onResponse(call: Call<Result<MyBatisPlusPage<User>>>, response: Response<Result<MyBatisPlusPage<User>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        binding.tvFollowingCount.text = getString(R.string.following_count_format, result.data?.total ?: 0)
                    } else {
                        Log.e("ProfileActivity", "Failed to fetch following count: ${result?.message}")
                        binding.tvFollowingCount.text = getString(R.string.following_count_format, 0) // Show 0 on error
                    }
                } else {
                    Log.e("ProfileActivity", "HTTP Error fetching following count: ${response.code()}")
                    binding.tvFollowingCount.text = getString(R.string.following_count_format, 0) // Show 0 on error
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                Log.e("ProfileActivity", "Network Failure fetching following count: ${t.message}", t)
                binding.tvFollowingCount.text = getString(R.string.following_count_format, 0) // Show 0 on error
            }
        })

        // Fetch Follower Count
        userFollowService.getFollowerUsers(userId, pageNum = 1, pageSize = 1).enqueue(object : Callback<Result<MyBatisPlusPage<User>>> {
            override fun onResponse(call: Call<Result<MyBatisPlusPage<User>>>, response: Response<Result<MyBatisPlusPage<User>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        binding.tvFollowersCount.text = getString(R.string.followers_count_format, result.data?.total ?: 0)
                    } else {
                        Log.e("ProfileActivity", "Failed to fetch follower count: ${result?.message}")
                        binding.tvFollowersCount.text = getString(R.string.followers_count_format, 0) // Show 0 on error
                    }
                } else {
                    Log.e("ProfileActivity", "HTTP Error fetching follower count: ${response.code()}")
                    binding.tvFollowersCount.text = getString(R.string.followers_count_format, 0) // Show 0 on error
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                Log.e("ProfileActivity", "Network Failure fetching follower count: ${t.message}", t)
                binding.tvFollowersCount.text = getString(R.string.followers_count_format, 0) // Show 0 on error
            }
        })
    }

    private fun logoutUser() {
        SessionManager.getInstance().clear() // Clear all login info from SessionManager
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
