package com.example.SaluAPP;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment {
    private String id;
    private String message;

    private String name;
    private String userId;
    private int likesCount;
    private List<String> likedUserIds;
    private Date date;
    private boolean isLiked;
    private String profilePictureUrl;

    public Comment() {
        // Constructor vacío requerido por Firebase
        likedUserIds = new ArrayList<>();
    }

    public Comment(String id, String message, String userId) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.likesCount = 0;
        this.likedUserIds = new ArrayList<>();
        this.date = new Date();
    }


    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLikedUserIds() {
        return likedUserIds;
    }

    public void incrementLikes() {
        likesCount++;
    }

    public void decrementLikes() {
        if (likesCount > 0) {
            likesCount--;
        }
    }
    public boolean isLiked() {
        return isLiked;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void toggleLiked() {
        isLiked = !isLiked;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", likesCount=" + likesCount +
                ", date=" + date +
                '}';
    }
}
