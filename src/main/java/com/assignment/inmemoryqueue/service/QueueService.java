package com.assignment.inmemoryqueue.service;

import com.assignment.inmemoryqueue.model.Consumer;

public interface QueueService {
    public void subscribe(String pattern, Consumer consumer);
}
