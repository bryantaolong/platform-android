package com.bryan.platform.activity.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.R
import com.bryan.platform.databinding.ActivityUserProfileBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.MyBatisPlusPage
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.network.UserFollowService
import com.bryan.platform.network.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val userService = RetrofitClient.createService(UserService::class.java)
    private val userFollowService = RetrofitClient.createService(UserFollowService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getLongExtra("userId", -1)
        if (userId == -1L) {
            Toast.makeText(this, "用户 ID 无效", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.progressBar.visibility = View.VISIBLE

        userService.getUserById(userId).enqueue(object : Callback<Result<User>> {
            override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
                binding.progressBar.visibility = View.GONE
                val user = response.body()?.data
                if (user != null) {
                    binding.tvUsername.text = user.username
                    binding.tvBio.text = "这个用户还没有写简介"
                    fetchFollowCounts(user.id) // Fetch follow counts using the ID from API
                } else {
                    Toast.makeText(this@UserProfileActivity, "获取用户信息失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Result<User>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UserProfileActivity, "网络请求失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.llFollowingCountClickable.setOnClickListener {
            userId?.let { id ->
                val intent = Intent(this, FollowingListActivity::class.java)
                intent.putExtra("userId", id) // Pass the user ID to the list activity
                startActivity(intent)
            } ?: Toast.makeText(this, "用户ID未加载，无法查看关注列表", Toast.LENGTH_SHORT).show()
        }

        binding.llFollowersCountClickable.setOnClickListener {
            userId?.let { id ->
                val intent = Intent(this, FollowersListActivity::class.java)
                intent.putExtra("userId", id) // Pass the user ID to the list activity
                startActivity(intent)
            } ?: Toast.makeText(this, "用户ID未加载，无法查看粉丝列表", Toast.LENGTH_SHORT).show()
        }
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
                        Log.e("UserProfileActivity", "Failed to fetch following count: ${result?.message}")
                        binding.tvFollowingCount.text = getString(R.string.following_count_format, 0) // Show 0 on error
                    }
                } else {
                    Log.e("UserProfileActivity", "HTTP Error fetching following count: ${response.code()}")
                    binding.tvFollowingCount.text = getString(R.string.following_count_format, 0) // Show 0 on error
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                Log.e("UserProfileActivity", "Network Failure fetching following count: ${t.message}", t)
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
                        Log.e("UserProfileActivity", "Failed to fetch follower count: ${result?.message}")
                        binding.tvFollowersCount.text = getString(R.string.followers_count_format, 0) // Show 0 on error
                    }
                } else {
                    Log.e("UserProfileActivity", "HTTP Error fetching follower count: ${response.code()}")
                    binding.tvFollowersCount.text = getString(R.string.followers_count_format, 0) // Show 0 on error
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                Log.e("UserProfileActivity", "Network Failure fetching follower count: ${t.message}", t)
                binding.tvFollowersCount.text = getString(R.string.followers_count_format, 0) // Show 0 on error
            }
        })
    }
}
