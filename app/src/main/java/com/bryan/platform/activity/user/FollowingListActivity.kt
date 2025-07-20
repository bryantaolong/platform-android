package com.bryan.platform.activity.user

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bryan.platform.R
import com.bryan.platform.databinding.ActivityFollowingListBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.MyBatisPlusPage
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.network.UserFollowService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFollowingListBinding
    private val userFollowService: UserFollowService = RetrofitClient.createService(UserFollowService::class.java)

    private var userId: Long = -1 // User ID whose following list we want to display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowingListBinding.inflate(layoutInflater)
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

        binding.toolbar.title = getString(R.string.following_list_title) // Set toolbar title

        fetchFollowingList(userId)
    }

    private fun fetchFollowingList(userId: Long) {
        // In a real app, you would use a RecyclerView and an Adapter here
        // For now, just fetching and logging the data.
        userFollowService.getFollowingUsers(userId, pageNum = 1, pageSize = 20).enqueue(object : Callback<Result<MyBatisPlusPage<User>>> {
            override fun onResponse(call: Call<Result<MyBatisPlusPage<User>>>, response: Response<Result<MyBatisPlusPage<User>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val followingUsers = result.data?.records ?: emptyList()
                        Log.d("FollowingListActivity", "Fetched ${followingUsers.size} following users for user ID: $userId")
                        // TODO: Display these users in a RecyclerView
                        binding.tvListContent.text = "关注列表 (总数: ${result.data?.total}):\n" +
                                followingUsers.joinToString("\n") { it.username }
                    } else {
                        val errorMsg = result?.message ?: "获取关注列表失败"
                        Toast.makeText(this@FollowingListActivity, errorMsg, Toast.LENGTH_LONG).show()
                        Log.e("FollowingListActivity", "API Error: $errorMsg")
                        binding.tvListContent.text = "获取关注列表失败"
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@FollowingListActivity, errorMsg, Toast.LENGTH_LONG).show()
                    Log.e("FollowingListActivity", errorMsg)
                    binding.tvListContent.text = "获取关注列表失败"
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                val errorMsg = "网络请求失败: ${t.message}"
                Toast.makeText(this@FollowingListActivity, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("FollowingListActivity", errorMsg, t)
                binding.tvListContent.text = "网络请求失败"
            }
        })
    }
}
