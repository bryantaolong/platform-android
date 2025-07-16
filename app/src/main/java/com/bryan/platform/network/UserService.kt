package com.bryan.platform.network

import com.bryan.platform.model.entity.User
import com.bryan.platform.model.request.ChangePasswordRequest
import com.bryan.platform.model.request.UserExportRequest
import com.bryan.platform.model.request.UserUpdateRequest
import com.bryan.platform.model.response.MyBatisPlusPage
import com.bryan.platform.model.response.Result
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 用户服务接口，定义了与用户相关的 API 端点。
 * 对应后端 UserController。
 */
interface UserService {

    /**
     * 获取所有用户列表（不分页）。
     * 对应后端 GET /api/user/all
     *
     * @return 包含所有用户数据的分页对象。
     */
    @GET("api/user/all")
    fun getAllUsers(): Call<Result<MyBatisPlusPage<User>>>

    /**
     * 根据用户 ID 查询用户信息。
     * 对应后端 GET /api/user/{userId}
     *
     * @param userId 目标用户ID
     * @return 对应用户实体
     */
    @GET("api/user/{userId}")
    fun getUserById(@Path("userId") userId: Long): Call<Result<User>>

    /**
     * 根据用户名查询用户信息。
     * 对应后端 GET /api/user/username/{username}
     *
     * @param username 用户名
     * @return 对应用户实体
     */
    @GET("api/user/username/{username}")
    fun getUserByUsername(@Path("username") username: String): Call<Result<User>>

    /**
     * 更新用户基本信息。
     * 对应后端 PUT /api/user/{userId}
     *
     * @param userId 目标用户ID
     * @param userUpdateRequest 包含需要更新的信息（用户名、邮箱等）
     * @return 更新后的用户实体
     */
    @PUT("api/user/{userId}")
    fun updateUser(
        @Path("userId") userId: Long,
        @Body userUpdateRequest: UserUpdateRequest
    ): Call<Result<User>>

    /**
     * 修改用户角色。
     * 对应后端 PUT /api/user/{userId}/role
     *
     * @param userId 目标用户ID
     * @param roles 新角色字符串，逗号分隔（如 "ROLE_USER,ROLE_ADMIN"）
     * @return 更新后的用户实体
     */
    @PUT("api/user/{userId}/role")
    fun changeRole(
        @Path("userId") userId: Long,
        @Body roles: String // 后端接收 String 类型的 Body
    ): Call<Result<User>>

    /**
     * 修改用户密码。
     * 对应后端 PUT /api/user/{userId}/password
     *
     * @param userId 目标用户ID
     * @param changePasswordRequest 包含旧密码和新密码的请求体
     * @return 更新后的用户实体
     */
    @PUT("api/user/{userId}/password")
    fun changePassword(
        @Path("userId") userId: Long,
        @Body changePasswordRequest: ChangePasswordRequest
    ): Call<Result<User>>

    /**
     * 删除用户（逻辑删除）。
     * 对应后端 DELETE /api/user/{userId}
     *
     * @param userId 目标用户ID
     * @return 被删除的用户实体
     */
    @DELETE("api/user/{userId}")
    fun deleteUser(@Path("userId") userId: Long): Call<Result<User>>

    /**
     * 导出用户数据，支持字段选择。
     * 对应后端 POST /api/user/export
     *
     * @param request 导出请求体，包含导出字段和筛选条件
     * @return ResponseBody 用于处理文件下载
     */
    @POST("api/user/export")
    fun exportUsers(@Body request: UserExportRequest): Call<ResponseBody>

    /**
     * 导出所有用户数据，包含所有字段。
     * 对应后端 GET /api/user/export/all
     *
     * @param status 用户状态筛选，非必填
     * @param fileName 导出文件名，默认 "用户数据"
     * @return ResponseBody 用于处理文件下载
     */
    @GET("api/user/export/all")
    fun exportAllUsers(
        @Query("status") status: Int? = null,
        @Query("fileName") fileName: String? = null
    ): Call<ResponseBody>

    /**
     * 获取可供导出的字段列表，供前端动态选择。
     * 对应后端 GET /api/user/export/fields
     *
     * @return 字段名与中文描述的映射表
     */
    @GET("api/user/export/fields")
    fun getExportFields(): Call<Result<Map<String, String>>>
}
