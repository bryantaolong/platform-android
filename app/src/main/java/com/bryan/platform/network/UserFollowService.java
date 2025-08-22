package com.bryan.platform.network;

import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.response.MyBatisPage;
import com.bryan.platform.model.response.Result;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 用户关注服务接口，定义了与用户关注相关的 API 端点。
 * 对应后端 UserFollowController。
 */
public interface UserFollowService {

    /**
     * 当前用户关注指定用户。
     * 对应后端 POST /api/user_follow/follow/{followingId}
     *
     * @param followingId 被关注用户ID
     * @return 关注操作是否成功，true表示成功
     */
    @POST("api/user_follow/follow/{followingId}")
    Call<Result<Boolean>> followUser(@Path("followingId") Long followingId);

    /**
     * 当前用户取消关注指定用户。
     * 对应后端 POST /api/user_follow/unfollow/{followingId}
     *
     * @param followingId 被取消关注用户ID
     * @return 取消关注是否成功，true表示成功
     */
    @POST("api/user_follow/unfollow/{followingId}")
    Call<Result<Boolean>> unfollowUser(@Path("followingId") Long followingId);

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
    Call<Result<MyBatisPage<User>>> getFollowingUsers(
        @Path("userId") Long userId,
        @Query("pageNum") int pageNum,
        @Query("pageSize") int pageSize
    );

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
    Call<Result<MyBatisPage<User>>> getFollowerUsers(
        @Path("userId") Long userId,
        @Query("pageNum") int pageNum,
        @Query("pageSize") int pageSize
    );

    /**
     * 检查当前用户是否关注指定用户。
     * 对应后端 GET /api/user_follow/check/{followingId}
     *
     * @param followingId 被检查的目标用户ID
     * @return true表示已关注，false表示未关注
     */
    @GET("api/user_follow/check/{followingId}")
    Call<Result<Boolean>> isFollowing(@Path("followingId") Long followingId);
}
