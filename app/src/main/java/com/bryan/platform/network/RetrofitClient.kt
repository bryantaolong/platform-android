package com.bryan.platform.network

import com.bryan.platform.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 客户端单例对象，用于创建 API 服务实例
 * 已整合认证拦截器和会话管理
 */
object RetrofitClient {

    // 后端 API 的基础 URL
    private const val BASE_URL = "http://10.0.2.2:8080/" // 模拟器访问宿主机器的本地服务地址

    // 不需要认证的公共 API 路径列表
    private val PUBLIC_URLS = listOf(
        "/api/auth/login",
        "/api/auth/register"
    )

    // 认证拦截器，用于自动添加 Token 到请求头
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestPath = originalRequest.url.encodedPath // 获取请求路径

            // 检查当前请求路径是否在公共 URL 列表中
            val isPublicUrl = PUBLIC_URLS.any { publicPath ->
                requestPath.endsWith(publicPath) // 检查请求路径是否以公共路径结尾
            }

            // 如果是公共 URL，则不添加 Token，直接继续请求
            if (isPublicUrl) {
                return chain.proceed(originalRequest)
            }

            // 如果不是公共 URL，则尝试添加 Token
            val token = SessionManager.getInstance().fetchAuthToken()

            return if (token != null) {
                // 如果 Token 存在，添加到请求头
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                // 没有 Token 则直接继续请求 (对于需要认证但没有 Token 的情况)
                chain.proceed(originalRequest)
            }
        }
    }

    // OkHttpClient 配置
    private val okHttpClient: OkHttpClient by lazy {
        // 创建 HttpLoggingInterceptor 实例，用于记录网络请求和响应的日志
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 设置日志级别为 BODY，表示记录请求和响应头以及正文
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()) // 添加认证拦截器
            .addInterceptor(loggingInterceptor) // 添加日志拦截器
            .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时
            .readTimeout(30, TimeUnit.SECONDS)    // 设置读取超时
            .writeTimeout(30, TimeUnit.SECONDS)   // 设置写入超时
            .build()
    }

    // Retrofit 实例，懒加载
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 设置基础 URL
            .client(okHttpClient) // 设置自定义的 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // 添加 Gson 转换器
            .build()
    }

    /**
     * 创建指定 API 服务的实例
     * @param serviceClass 接口类的 Class 对象
     * @return API 服务的实例
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    /**
     * Kotlin 扩展函数方式创建服务 (更简洁)
     */
    inline fun <reified T> createService(): T {
        return createService(T::class.java)
    }
}
