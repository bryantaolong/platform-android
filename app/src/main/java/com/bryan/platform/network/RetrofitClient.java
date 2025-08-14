package com.bryan.platform.network;

import androidx.annotation.NonNull;

import com.bryan.platform.utils.SessionManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 客户端单例，用于创建 API 服务实例
 * 已整合认证拦截器和会话管理
 */
public final class RetrofitClient {

    // 后端 API 的基础 URL
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    // 不需要认证的公共 API 路径列表
    private static final List<String> PUBLIC_URLS = Arrays.asList(
    "/api/auth/login",
    "/api/auth/register"
    );

    private volatile static RetrofitClient INSTANCE;
    private final Retrofit retrofit;

    private RetrofitClient() {
        // 日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        // Retrofit
        retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public static RetrofitClient getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitClient();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 创建指定 API 服务的实例
     */
    public <T> T createService(@NonNull Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    /* ========================= 内部类：认证拦截器 ========================= */

    private static class AuthInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws java.io.IOException {
            Request original = chain.request();
            String path = original.url().encodedPath();

            // 公共接口直接放行
            boolean isPublic = false;
            for (String publicPath : PUBLIC_URLS) {
            if (path.endsWith(publicPath)) {
                isPublic = true;
                break;
            }
        }
            if (isPublic) {
                return chain.proceed(original);
            }

            // 非公共接口：需要 token
            String token = SessionManager.getInstance().fetchAuthToken();
            if (token != null) {
                Request newRequest = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
                return chain.proceed(newRequest);
            }

            // 没有 token 也放行（由后端决定 401/403）
            return chain.proceed(original);
        }
    }
}