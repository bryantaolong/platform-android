package com.bryan.platform.activity.user

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.R
import com.bryan.platform.model.entity.User
import com.bryan.platform.databinding.ActivityFollowersListBinding
import com.bryan.platform.model.response.MyBatisPlusPage
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.network.UserFollowService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFollowersListBinding
    private val userFollowService: UserFollowService = RetrofitClient.createService(UserFollowService::class.java)

    private var userId: Long = -1 // User ID whose followers list we want to display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        userId = intent.getLongExtra("userId", -1L)
        if (userId == -1L) {
            Toast.makeText(this, "用户ID无效", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.toolbar.title = getString(R.string.followers_list_title) // Set toolbar title

        fetchFollowersList(userId)
    }

    private fun fetchFollowersList(userId: Long) {
        // In a real app, you would use a RecyclerView and an Adapter here
        // For now, just fetching and logging the data.
        userFollowService.getFollowerUsers(userId, pageNum = 1, pageSize = 20).enqueue(object : Callback<Result<MyBatisPlusPage<User>>> {
            override fun onResponse(call: Call<Result<MyBatisPlusPage<User>>>, response: Response<Result<MyBatisPlusPage<User>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val followerUsers = result.data?.records ?: emptyList()
                        Log.d("FollowersListActivity", "Fetched ${followerUsers.size} follower users for user ID: $userId")
                        // TODO: Display these users in a RecyclerView
                        binding.tvListContent.text = "粉丝列表 (总数: ${result.data?.total}):\n" +
                                followerUsers.joinToString("\n") { it.username }
                    } else {
                        val errorMsg = result?.message ?: "获取粉丝列表失败"
                        Toast.makeText(this@FollowersListActivity, errorMsg, Toast.LENGTH_LONG).show()
                        Log.e("FollowersListActivity", "API Error: $errorMsg")
                        binding.tvListContent.text = "获取粉丝列表失败"
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@FollowersListActivity, errorMsg, Toast.LENGTH_LONG).show()
                    Log.e("FollowersListActivity", errorMsg)
                    binding.tvListContent.text = "获取粉丝列表失败"
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                val errorMsg = "网络请求失败: ${t.message}"
                Toast.makeText(this@FollowersListActivity, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("FollowersListActivity", errorMsg, t)
                binding.tvListContent.text = "网络请求失败"
            }
        })
    }
}
