package com.assignment.service;

import com.assignment.model.Consumer;

public interface QueueService {
    public void subscribe(String pattern, Consumer consumer);
}
