package com.bryan.platform.model.response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * 统一响应封装数据模型（Java 版）
 * 对应后端 Result 泛型类
 */
public class Result<T> {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    @Nullable
    private T data;

    public Result() {
    }

    public Result(int code, String message, @Nullable T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nullable
    public T getData() {
        return data;
    }

    public void setData(@Nullable T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code == 200;
    }

    @NonNull
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}