package com.example.qsort;

public class VoiceComment {
    String timestamp;
    String storageRef;

    public VoiceComment(String timestamp, String storageRef) {
        this.timestamp = timestamp;
        this.storageRef = storageRef;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStorageRef() {
        return storageRef;
    }

    public void setStorageRef(String storageRef) {
        this.storageRef = storageRef;
    }
}
