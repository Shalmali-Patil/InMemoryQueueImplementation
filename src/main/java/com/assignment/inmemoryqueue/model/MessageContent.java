package com.assignment.inmemoryqueue.model;

public class MessageContent {
    private String messageId;
    private String httpCode;

    public MessageContent(String messageId, String httpCode) {
        this.messageId = messageId;
        this.httpCode = httpCode;
    }

    public MessageContent() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }
}
