package com.assignment.model;

public class Message {
    private String messageId;
    private String content;
    private long createdAt;

    private int retryCount = 0;
    public static final int MAX_RETRY_COUNT = 3;

    public Message(String messageId) {
        this.messageId = messageId;
        // TODO: replace with JSON content
        this.content = messageId;
        this.createdAt = System.currentTimeMillis();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
