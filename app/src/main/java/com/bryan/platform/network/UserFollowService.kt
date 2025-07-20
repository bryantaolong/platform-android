package com.bryan.platform.network

import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.MyBatisPlusPage
import com.bryan.platform.model.response.Result
import retrofit2.Call
import retrofit2.http.*

/**
 * 用户关注服务接口，定义了与用户关注相关的 API 端点。
 * 对应后端 UserFollowController。
 */
interface UserFollowService {

    /**
     * 当前用户关注指定用户。
     * 对应后端 POST /api/user_follow/follow/{followingId}
     *
     * @param followingId 被关注用户ID
     * @return 关注操作是否成功，true表示成功
     */
    @POST("api/user_follow/follow/{followingId}")
    fun followUser(
        @Path("followingId") followingId: Long
    ): Call<Result<Boolean>>

    /**
     * 当前用户取消关注指定用户。
     * 对应后端 POST /api/user_follow/unfollow/{followingId}
     *
     * @param followingId 被取消关注用户ID
     * @return 取消关注是否成功，true表示成功
     */
    @POST("api/user_follow/unfollow/{followingId}")
    fun unfollowUser(
        @Path("followingId") followingId: Long
    ): Call<Result<Boolean>>

    /**
     * 查询指定用户关注的用户列表（分页）。
     * 对应后端 GET /api/user_follow/following/{userId}
     *
     * @param userId 目标用户ID
     * @param pageNum 页码，默认 1
     * @param pageSize 每页大小，默认 10
     * @return 分页的关注用户列表
     */
    @GET("api/user_follow/following/{userId}")
    fun getFollowingUsers(
        @Path("userId") userId: Long,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Call<Result<MyBatisPlusPage<User>>> // Using your Android Page<User> model

    /**
     * 查询指定用户的粉丝列表（分页）。
     * 对应后端 GET /api/user_follow/followers/{userId}
     *
     * @param userId 目标用户ID
     * @param pageNum 页码，默认 1
     * @param pageSize 每页大小，默认 10
     * @return 分页的粉丝用户列表
     */
    @GET("api/user_follow/followers/{userId}")
    fun getFollowerUsers(
        @Path("userId") userId: Long,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Call<Result<MyBatisPlusPage<User>>> // Using your Android Page<User> model

    /**
     * 检查当前用户是否关注指定用户。
     * 对应后端 GET /api/user_follow/check/{followingId}
     *
     * @param followingId 被检查的目标用户ID
     * @return true表示已关注，false表示未关注
     */
    @GET("api/user_follow/check/{followingId}")
    fun isFollowing(
        @Path("followingId") followingId: Long
    ): Call<Result<Boolean>>
}
