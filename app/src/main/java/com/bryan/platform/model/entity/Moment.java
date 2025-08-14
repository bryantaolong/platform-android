package com.bryan.platform.model.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 动态(朋友圈)实体数据模型（Android 客户端用）
 * 对应后端的 Moment MongoDB 文档
 */
public class Moment implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("images")
    private String images;          // JSON 字符串，客户端按需解析

    @SerializedName("authorId")
    private Long authorId;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("likeCount")
    private Integer likeCount = 0;

    @SerializedName("comments")
    private List<Comment> comments = Collections.emptyList();

    // 注意：后端字段为 snake_case
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public Moment() {
    }

    public Moment(String id,
    String content,
    String images,
    Long authorId,
    String authorName,
    Integer likeCount,
    List<Comment> comments,
    String createdAt,
    String updatedAt) {
        this.id = id;
        this.content = content;
        this.images = images;
        this.authorId = authorId;
        this.authorName = authorName;
        this.likeCount = likeCount;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    @NonNull
    @Override
    public String toString() {
        return "Moment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", images='" + images + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", likeCount=" + likeCount +
                ", comments=" + comments +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}