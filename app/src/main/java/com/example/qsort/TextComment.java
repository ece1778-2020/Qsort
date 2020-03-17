package com.example.qsort;

public class TextComment {
    String timestamp;
    String textComment;

    public TextComment(String timestamp, String textComment) {
        this.timestamp = timestamp;
        this.textComment = textComment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }
}
