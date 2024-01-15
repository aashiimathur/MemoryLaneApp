package com.example.journalapp;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Journal implements Parcelable {

    private String title;
    private String documentId;

    private String thoughts;
    private String imageUrl;
    private String userId;
    private Timestamp timeAdded;
    private String userName;

    // When using Firebase, always create an empty constructor
    public Journal() {
    }

    public Journal(String title, String thoughts, String imageUrl, String userId, Timestamp timeAdded, String userName, String documentId) {
        this.title = title;
        this.thoughts = thoughts;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.userName = userName;
        this.documentId=documentId;
    }

    protected Journal(Parcel in) {
        title = in.readString();
        thoughts = in.readString();
        imageUrl = in.readString();
        userId = in.readString();
        timeAdded = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        userName = in.readString();
        documentId=in.readString();
    }

    public static final Creator<Journal> CREATOR = new Creator<Journal>() {
        @Override
        public Journal createFromParcel(Parcel in) {
            return new Journal(in);
        }

        @Override
        public Journal[] newArray(int size) {
            return new Journal[size];
        }
    };

    public String getTitle() {
        return title;
    }
    public String getDocumentId() {
        return documentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(thoughts);
        dest.writeString(imageUrl);
        dest.writeString(userId);
        dest.writeValue(timeAdded);
        dest.writeString(userName);
        dest.writeString(documentId);
    }
}
