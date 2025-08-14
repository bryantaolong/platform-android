package com.bryan.platform.model.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 评论实体数据模型（Android 客户端用）
 * 对应后端 Moment 实体中的内嵌 Comment 文档
 */
public class Comment implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("authorId")
    private Long authorId;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("createdAt")
    private String createdAt;

    public Comment() {
    }

    public Comment(String id,
                   String content,
                   Long authorId,
                   String authorName,
                   String createdAt) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}