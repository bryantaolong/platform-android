package com.bryan.platform.network;

import androidx.annotation.NonNull;

import com.bryan.platform.model.entity.Moment;
import com.bryan.platform.model.response.SpringPage;
import com.bryan.platform.model.response.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 动态(Moment)服务接口，定义了与动态相关的 RESTful API 端点。
 * 备注: 需要认证的接口，其认证 Token 通常通过 OkHttp Interceptor 全局添加，
 * 因此接口定义中不显式包含 Authorization Header。
 */
public interface MomentService {

    /**
     * 创建一条新的动态。
     * @param moment 包含动态内容的对象。注意：authorId 和 authorName 应由后端根据token设置。
     * @return 返回创建成功的动态数据。
     * @see <a href="http://yourapi.com/docs/moments/create">API Doc: Create Moment</a>
     * @requiresAuth 需要用户登录认证。
     */
    @POST("api/moments")
    Call<Result<Moment>> createMoment(@Body Moment moment);

    /**
     * 分页查询所有动态。
     * @param page 页码（从0开始，默认0）。
     * @param size 每页数量（默认10）。
     * @param sort 排序规则（格式："字段名,desc/asc"，例如 "createdAt,desc"）。
     * @return 返回动态的分页结果。
     */
    @GET("api/moments")
    Call<Result<SpringPage<Moment>>> getAllMoments(
        @Query("page") int page,
        @Query("size") int size,
        @Query("sort") @NonNull String sort
    );

    /**
     * 根据ID获取动态详情。
     * @param id 动态的唯一ID。
     * @return 返回指定ID的动态详情。
     */
    @GET("api/moments/{id}")
    Call<Result<Moment>> getMomentById(@Path("id") String id);

    /**
     * 分页获取指定用户的动态列表。
     * @param userId 用户的ID。
     * @param page 页码（从0开始，默认0）。
     * @param size 每页数量（默认10）。
     * @return 返回该用户的动态分页结果。
     */
    @GET("api/moments/user/{userId}")
    Call<Result<SpringPage<Moment>>> getUserMoments(
        @Path("userId") Long userId,
        @Query("page") int page,
        @Query("size") int size
    );

    /**
     * 获取当前登录用户的动态列表。
     * @param page 页码（从0开始，默认0）。
     * @param size 每页数量（默认10）。
     * @return 返回当前用户的动态分页结果。
     * @requiresAuth 需要用户登录认证。
     */
    @GET("api/moments/me")
    Call<Result<SpringPage<Moment>>> getMyMoments(
            @Query("page") int page,
            @Query("size") int size
    );

    /**
     * 获取关注的好友的动态流。
     * @param page 页码（从0开始，默认0）。
     * @param size 每页数量（默认10）。
     * @return 返回好友动态的分页结果。
     * @requiresAuth 需要用户登录认证。
     */
    @GET("api/moments/following")
    Call<Result<SpringPage<Moment>>> getFollowingMoments(
            @Query("page") int page,
            @Query("size") int size
    );

    /**
     * 根据ID列表批量获取动态。
     * @param ids 包含多个动态ID的列表。
     * @return 返回匹配的动态列表。
     */
    @POST("api/moments/batch")
    Call<Result<List<Moment>>> getMomentsByIds(@Body List<String> ids);

    /**
     * 删除一条动态。
     * @param id 要删除的动态的唯一ID。
     * @return 返回操作结果，data部分为null。
     * @requiresAuth 需要作者或管理员权限。
     */
    @DELETE("api/moments/{id}")
    Call<Result<Void>> deleteMoment(@Path("id") String id);

    /**
     * 根据关键词搜索动态内容。
     * @param keyword 搜索的关键词。
     * @return 返回匹配的动态列表。
     */
    @GET("api/moments/search")
    Call<Result<List<Moment>>> searchMoments(@Query("keyword") String keyword);

    /**
     * 获取热门动态。
     * @param minLikes 最小点赞数过滤条件（默认100）。
     * @param page 页码（从0开始，默认0）。
     * @param size 每页数量（默认10）。
     * @return 返回热门动态的分页结果。
     */
    @GET("api/moments/popular")
    Call<Result<SpringPage<Moment>>> getPopularMoments(
        @Query("minLikes") int minLikes,
        @Query("page") int page,
        @Query("size") int size
    );
}