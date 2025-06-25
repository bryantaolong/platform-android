package com.bryan.platform_android.network // 使用您的命名空间

import com.bryan.platform_android.model.request.LoginRequest
import com.bryan.platform_android.model.request.RegisterRequest
import com.bryan.platform_android.model.User
import com.bryan.platform_android.model.response.Result
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 认证服务接口，定义了与用户认证相关的 API 端点
 */
interface AuthService {

    /**
     * 用户登录接口
     * @param loginRequest 登录请求体，包含用户名和密码
     * @return Retrofit Call 对象，成功时返回 Result<String> (String 为 JWT Token)
     */
    @POST("api/user/login")
    fun login(@Body loginRequest: LoginRequest): Call<Result<String>>

    /**
     * 用户注册接口
     * @param registerRequest 注册请求体，包含用户名、密码和邮箱
     * @return Retrofit Call 对象，成功时返回 Result<User> (User 为注册成功的用户实体)
     */
    @POST("api/user/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Result<User>>
}