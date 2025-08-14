package com.bryan.platform.model.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * 用户实体类模型（Android/客户端用）
 * 字段顺序与后端保持一致，方便双向映射。
 */
public class User {

    /* ====== 原有字段（与 Kotlin data class 顺序一致） ====== */

    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("status")
    private Integer status;          // 0-正常，1-封禁，2-锁定（与后端枚举保持一致）

    @SerializedName("roles")
    private String roles;            // 逗号分隔的角色字符串

    @SerializedName("lastLoginAt")
    private String lastLoginAt;

    @SerializedName("lastLoginIp")
    private String lastLoginIp;

    @SerializedName("passwordResetAt")
    private String passwordResetAt;

    @SerializedName("loginFailCount")
    private Integer loginFailCount; // 登录失败次数

    @SerializedName("lockedAt")
    private String lockedAt; // 账户锁定时间

    @SerializedName("deleted")
    private Integer deleted;         // 逻辑删除标记

    @SerializedName("version")
    private String version;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("updatedBy")
    private String updatedBy;

    public User() {
    }

    public User(Long id, String username, String password, String phone,
                String email, Integer status, String roles, String lastLoginAt,
                String lastLoginIp, String passwordResetAt, Integer loginFailCount,
                String lockedAt, Integer deleted, String version, String createdAt,
                String updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.roles = roles;
        this.lastLoginAt = lastLoginAt;
        this.lastLoginIp = lastLoginIp;
        this.passwordResetAt = passwordResetAt;
        this.loginFailCount = loginFailCount;
        this.lockedAt = lockedAt;
        this.deleted = deleted;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getPasswordResetAt() {
        return passwordResetAt;
    }

    public void setPasswordResetAt(String passwordResetAt) {
        this.passwordResetAt = passwordResetAt;
    }

    public Integer getLoginFailCount() {
        return loginFailCount;
    }

    public void setLoginFailCount(Integer loginFailCount) {
        this.loginFailCount = loginFailCount;
    }

    public String getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(String lockedAt) {
        this.lockedAt = lockedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", roles='" + roles + '\'' +
                ", lastLoginAt='" + lastLoginAt + '\'' +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", passwordResetAt='" + passwordResetAt + '\'' +
                ", loginFailCount=" + loginFailCount +
                ", lockedAt='" + lockedAt + '\'' +
                ", deleted=" + deleted +
                ", version='" + version + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}