package com.bryan.platform.model.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * 用户关注实体类模型（Android/客户端用）
 * 对应后端 UserFollow Entity
 */
public class UserFollow {

    @SerializedName("id")
    private Long id;              // 主键ID

    @SerializedName("followerId")
    private Long followerId;      // 关注者ID

    @SerializedName("followingId")
    private Long followingId;     // 被关注者ID

    @SerializedName("deleted")
    private Integer deleted;      // 逻辑删除标记

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

    public UserFollow() {
    }

    public UserFollow(Long id,
    Long followerId,
    Long followingId,
    Integer deleted,
    String version,
    String createdAt,
    String updatedAt,
    String createdBy,
    String updatedBy) {
        this.id = id;
        this.followerId = followerId;
        this.followingId = followingId;
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

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
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
        return "UserFollow{" +
                "id=" + id +
                ", followerId=" + followerId +
                ", followingId=" + followingId +
                ", deleted=" + deleted +
                ", version='" + version + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}