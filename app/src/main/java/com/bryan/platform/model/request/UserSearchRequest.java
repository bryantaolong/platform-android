package com.bryan.platform.model.request;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * 用户搜索条件（Android 端请求体）
 *
 * @author Bryan Long
 */
public class UserSearchRequest {

    /* ---------- 基本字段 ---------- */

    @SerializedName("username")
    private String username;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    /**
     * 用户状态：0-正常，1-封禁，2-锁定
     */
    @SerializedName("status")
    private Integer status;

    @SerializedName("roles")
    private String roles;

    /* ---------- 时间字段（ISO-8601 字符串） ---------- */

    @SerializedName("lastLoginAt")
    private String lastLoginAt;

    @SerializedName("lastLoginIp")
    private String lastLoginIp;

    @SerializedName("passwordResetAt")
    private String passwordResetAt;

    /* ---------- 数值字段 ---------- */

    @SerializedName("loginFailCount")
    private Integer loginFailCount;

    @SerializedName("lockedAt")
    private String lockedAt;

    @SerializedName("deleted")
    private Integer deleted;

    @SerializedName("version")
    private Integer version;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("createTimeStart")
    private String createTimeStart;

    @SerializedName("createTimeEnd")
    private String createTimeEnd;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("updateTimeStart")
    private String updateTimeStart;

    @SerializedName("updateTimeEnd")
    private String updateTimeEnd;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("updatedBy")
    private String updatedBy;

    /* ---------- 构造 & Getter/Setter ---------- */

    public UserSearchRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdateTimeStart() {
        return updateTimeStart;
    }

    public void setUpdateTimeStart(String updateTimeStart) {
        this.updateTimeStart = updateTimeStart;
    }

    public String getUpdateTimeEnd() {
        return updateTimeEnd;
    }

    public void setUpdateTimeEnd(String updateTimeEnd) {
        this.updateTimeEnd = updateTimeEnd;
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

    /* ---------- 本地校验示例（可选） ---------- */

    /**
     * 简单校验时间范围是否成对出现
     */
    public boolean isTimeRangeValid() {
        return (createTimeStart == null) == (createTimeEnd == null)
                && (updateTimeStart == null) == (updateTimeEnd == null);
    }

    /* ---------- toString ---------- */

    @NonNull
    @Override
    public String toString() {
        return "UserSearchRequest{" +
                "username='" + username + '\'' +
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
                ", version=" + version +
                ", createdAt='" + createdAt + '\'' +
                ", createTimeStart='" + createTimeStart + '\'' +
                ", createTimeEnd='" + createTimeEnd + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", updateTimeStart='" + updateTimeStart + '\'' +
                ", updateTimeEnd='" + updateTimeEnd + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}