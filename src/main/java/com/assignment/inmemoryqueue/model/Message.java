package com.assignment.inmemoryqueue.model;

public class Message {
    private String messageId;
    private MessageContent content;
    private long createdAt;

    private int retryCount = 0;
    public static final int MAX_RETRY_COUNT = 3;

    public Message(String messageId, MessageContent content) {
        this.messageId = messageId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
    }

    public Message() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
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
