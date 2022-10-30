package com.assignment.model;

import java.util.List;

public class Consumer {
    private Queue queueWrapper;
    private final String consumerId;
    private boolean consumeFlag = true;
    private String pattern;
    private List<Consumer> dependencies;
    private String callbackMethod;

    public Consumer(Queue queueWrapper, String pattern, List<Consumer> dependencies, String callbackMethod, String consumerId) {
        this.queueWrapper = queueWrapper;
        this.pattern = pattern;
        this.dependencies = dependencies;
        this.callbackMethod = callbackMethod;
        this.consumerId = consumerId;
    }

    public void consume(Message m) {
        //while(consumeFlag) {

            /*if(queueWrapper.isEmpty()) {
                try {
                    queueWrapper.waitOnEmpty();
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException occurred in Consumer.java: " + e);
                    //break;
                }
            }*/
            /*if(!consumeFlag) {
                break;
            }*/
            //Message m = queueWrapper.peek();
            //Message m = queueWrapper.remove();

            //queueWrapper.notifyOnFull();
            if(m != null) {
                System.out.println("m is: " + m.getMessageId() + ", pattern is:" + this.pattern);
                if(m.getMessageId().contains(this.pattern)) {
                    //m = queueWrapper.remove();
                    System.out.println("Consumer: " + consumerId + " has consumed message with id: " + m.getMessageId() + ", thread name" + Thread.currentThread().getName());
                } else {
                    System.out.println("Consumer: " + consumerId + " did not match pattern "+this.pattern +" with message id: " + m.getMessageId() + ", thread name" + Thread.currentThread().getName());
                }
            }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occurred in Consumer.java");
        }
        //queueWrapper.notifyOnFull();
        //}
    }

    /*@Override
    public void run() {
        consume();
    }*/

    public void stopConsuming() {
        this.consumeFlag = false;
        queueWrapper.notifyOnEmpty();
    }

    public List<Consumer> getDependencies() {
        return dependencies;
    }

    public String getCallbackMethod() {
        return callbackMethod;
    }

    public String getConsumerId() {
        return consumerId;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "consumerId='" + consumerId + '\'' +
                ", pattern='" + pattern + '\'' +
                ", dependencies=" + dependencies +
                '}';
    }

    public String getPattern() {
        return pattern;
    }
}
