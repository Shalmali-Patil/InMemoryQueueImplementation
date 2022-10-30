package com.assignment.model;

import java.util.List;

public class Consumer1 extends Consumer {

    public Consumer1(Queue queueWrapper, String pattern, List<Consumer> dependencies, String callbackMethod, String consumerId) {
        super(queueWrapper, pattern, dependencies, callbackMethod, consumerId);
    }

    public void consume1(Message m) {
        System.out.println("This is consume1 method");
        if(m != null) {
            System.out.println("m is: " + m.getMessageId() + ", pattern is:" + getPattern());
            if(m.getMessageId().contains(getPattern())) {
                System.out.println("CONSUMED!!!Consumer: " + getConsumerId() + " has consumed message with id: " + m.getMessageId() + ", thread name" + Thread.currentThread().getName());
            } else {
                System.out.println("Consumer: " + getConsumerId() + " did not match pattern "+ getPattern() +" with message id: " + m.getMessageId() + ", thread name" + Thread.currentThread().getName());
            }
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occurred in Consumer.java");
        }
    }
}
