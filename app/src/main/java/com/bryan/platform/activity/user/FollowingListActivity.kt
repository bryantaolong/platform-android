package com.bryan.platform.activity.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryan.platform.R
import com.bryan.platform.adapter.UserFollowAdapter
import com.bryan.platform.databinding.ActivityFollowingListBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.MyBatisPlusPage
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.network.UserFollowService
import com.bryan.platform.utils.SessionManager // Import SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFollowingListBinding
    private lateinit var userFollowAdapter: UserFollowAdapter
    private val userFollowService: UserFollowService = RetrofitClient.createService(UserFollowService::class.java)

    private var userId: Long = -1 // User ID whose following list we want to display
    private var currentLoggedInUserId: Long = -1 // Current logged-in user's ID

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

        // Get current logged-in user ID from SessionManager (assuming it's stored there after login)
        // Or fetch it from AuthService if not available locally
        val storedUserId = SessionManager.getInstance().fetchAuthToken()?.let {
            // This is a placeholder. You need to store the user ID in SessionManager during login
            // or fetch it from AuthService. For now, we'll assume the userId passed via Intent is the current user's ID
            // if we are viewing our own following list. If viewing another user's list, currentLoggedInUserId is needed.
            // For simplicity, let's assume the current user ID is the one whose list we're viewing for now.
            // In a real app, you'd have a dedicated way to get the logged-in user's ID.
            userId // Placeholder: assuming we are viewing our own list for simplicity
        } ?: -1L // Fallback if no user ID in session

        // If currentLoggedInUserId is not available, try to fetch it or ensure login
        if (currentLoggedInUserId == -1L) {
            // In a real app, you'd fetch the current user's ID from AuthService here
            // For now, if the list is for the current user, we use 'userId'.
            // If it's for another user, you'd need the actual logged-in user's ID.
            // Let's set it to userId for now if this is our own list, otherwise, it needs to be fetched.
            // For this example, we'll assume the list being viewed is always the current user's list,
            // or we're just displaying. The follow/unfollow logic needs the actual logged-in user's ID.
            // For demo, let's assume currentLoggedInUserId is available.
            // A more robust solution would involve passing currentLoggedInUserId via Intent or fetching it here.
            currentLoggedInUserId = SessionManager.getInstance().fetchAuthToken()?.let {
                // This is a placeholder. A proper SessionManager would store user ID.
                // For now, let's just use the userId passed, assuming it's the current user for follow checks.
                // This is a simplification for the demo.
                userId // Fallback to the list owner's ID for follow checks if actual logged-in ID isn't easily accessible
            } ?: -1L // If no token, then no logged-in user.
        }


        binding.toolbar.title = getString(R.string.following_list_title) // Set toolbar title

        setupRecyclerView()
        fetchFollowingList(userId)
    }

    private fun setupRecyclerView() {
        // Pass currentLoggedInUserId and isFollowingList=true
        userFollowAdapter = UserFollowAdapter(emptyList(), currentLoggedInUserId, true)
        binding.rvUserList.apply {
            adapter = userFollowAdapter
            layoutManager = LinearLayoutManager(this@FollowingListActivity)
            // Add a divider line between items
            addItemDecoration(DividerItemDecoration(this@FollowingListActivity, DividerItemDecoration.VERTICAL))
        }

        userFollowAdapter.onFollowStatusChanged = {
            // Refresh follow counts in ProfileActivity if it's still active
            // This is a simple way to notify, a more robust solution would use LiveData/ViewModel
            Log.d("FollowingListActivity", "Follow status changed, consider refreshing counts.")
            // If you want to refresh the list itself after a follow/unfollow:
            // fetchFollowingList(userId)
        }
    }

    private fun fetchFollowingList(userId: Long) {
        binding.progressBarList.visibility = View.VISIBLE
        userFollowService.getFollowingUsers(userId, pageNum = 1, pageSize = 20).enqueue(object : Callback<Result<MyBatisPlusPage<User>>> {
            override fun onResponse(call: Call<Result<MyBatisPlusPage<User>>>, response: Response<Result<MyBatisPlusPage<User>>>) {
                binding.progressBarList.visibility = View.GONE
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val followingUsers = result.data?.records ?: emptyList()
                        userFollowAdapter.updateData(followingUsers)
                        Log.d("FollowingListActivity", "Fetched ${followingUsers.size} following users for user ID: $userId")
                    } else {
                        val errorMsg = result?.message ?: "获取关注列表失败"
                        Toast.makeText(this@FollowingListActivity, errorMsg, Toast.LENGTH_LONG).show()
                        Log.e("FollowingListActivity", "API Error: $errorMsg")
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@FollowingListActivity, errorMsg, Toast.LENGTH_LONG).show()
                    Log.e("FollowingListActivity", errorMsg)
                }
            }

            override fun onFailure(call: Call<Result<MyBatisPlusPage<User>>>, t: Throwable) {
                binding.progressBarList.visibility = View.GONE
                val errorMsg = "网络请求失败: ${t.message}"
                Toast.makeText(this@FollowingListActivity, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("FollowingListActivity", errorMsg, t)
            }
        })
    }
}
