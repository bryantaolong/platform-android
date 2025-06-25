package com.bryan.platform_android.network // 使用您的命名空间

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 客户端单例对象，用于创建 API 服务实例
 */
object RetrofitClient {

    // 后端 API 的基础 URL
    // 请根据您的实际后端服务地址进行修改！
    // 如果在本地运行后端，并且 Android 模拟器访问本地服务，请使用 10.0.2.2
    // 如果是物理设备，请使用您的电脑的局域网 IP 地址，例如 "http://192.168.1.xxx:8080/"
    private const val BASE_URL = "http://10.0.2.2:8080/" // 模拟器访问宿主机器的本地服务地址

    // OkHttpClient 配置
    private val okHttpClient: OkHttpClient by lazy {
        // 创建 HttpLoggingInterceptor 实例，用于记录网络请求和响应的日志
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 设置日志级别为 BODY，表示记录请求和响应头以及正文
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
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
            .addConverterFactory(GsonConverterFactory.create()) // 添加 Gson 转换器，用于 JSON 序列化和反序列化
            .build()
    }

    /**
     * 创建指定 API 服务的实例
     * 使用范例：`val authService = RetrofitClient.createService(AuthService::class.java)`
     * @param serviceClass 接口类的 Class 对象
     * @return API 服务的实例
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}