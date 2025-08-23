package com.bryan.platform.network;

import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.request.*;
import com.bryan.platform.model.response.MyBatisPage;
import com.bryan.platform.model.response.Result;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 用户服务接口（与后端 UserController 完全对齐）
 */
public interface UserService {

    /* ---------------- 查询类接口 ---------------- */

    /** 获取所有用户（分页） */
    @POST("api/users/all")
    Call<Result<MyBatisPage<User>>> getAllUsers(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /** 根据 ID 查询 */
    @GET("api/users/{userId}")
    Call<Result<User>> getUserById(@Path("userId") Long userId);

    /** 根据用户名查询 */
    @GET("api/users/username/{username}")
    Call<Result<User>> getUserByUsername(@Path("username") String username);

    /** 多条件搜索 + 分页 */
    @POST("api/users/search")
    Call<Result<MyBatisPage<User>>> searchUsers(
            @Body UserSearchRequest searchRequest,
            @Query("pageNum") Long pageNum,
            @Query("pageSize") Long pageSize
    );

    /* ---------------- 更新类接口 ---------------- */

    /** 更新基本信息 */
    @PUT("api/users/{userId}")
    Call<Result<User>> updateUser(
            @Path("userId") Long userId,
            @Body UserUpdateRequest userUpdateRequest
    );

    /** 修改角色（管理员） */
    @PUT("api/users/roles/{userId}/")
    Call<Result<User>> changeRoleByIds(
            @Path("userId") Long userId,
            @Body ChangeRoleRequest roles
    );

    /** 普通修改密码（需提供旧密码） */
    @PUT("api/users/password/{userId}")
    Call<Result<User>> changePassword(
            @Path("userId") Long userId,
            @Body ChangePasswordRequest changePasswordRequest
    );

    /** 管理员强制修改密码 */
    @PUT("api/users/password/force/{userId}")
    Call<Result<User>> changePasswordForcefully(
            @Path("userId") Long userId,
            @Body ChangePasswordRequest changePasswordRequest
    );

    /* ---------------- 状态变更接口 ---------------- */

    /** 封禁用户 */
    @PUT("api/users/block/{userId}")
    Call<Result<User>> blockUser(@Path("userId") Long userId);

    /** 解封用户 */
    @PUT("api/users/unblock/{userId}")
    Call<Result<User>> unblockUser(@Path("userId") Long userId);

    /* ---------------- 删除接口 ---------------- */

    /** 逻辑删除用户 */
    @DELETE("api/users/{userId}")
    Call<Result<User>> deleteUser(@Path("userId") Long userId);

    /* ---------------- 导出接口 ---------------- */

    /** 按条件导出 */
    @POST("api/users/export")
    Call<ResponseBody> exportUsers(@Body UserExportRequest request);

    /** 导出全部 */
    @GET("api/users/export/all")
    Call<ResponseBody> exportAllUsers(
            @Query("status") Integer status,
            @Query("fileName") String fileName
    );

    /** 获取可导出字段列表 */
    @GET("api/users/export/fields")
    Call<Result<Map<String, String>>> getExportFields();
}