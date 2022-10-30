package com.assignment.model;

import com.assignment.service.QueueServiceImpl;

public class Producer implements Runnable {
    private Queue queue;
    private final String producerId;
    private boolean produceFlag = true;
    private Message message;
    private QueueServiceImpl queueServiceImpl;

    private Queue deadQueue;
    private final String[] messages = new String[]{"200","400","400","200","200","400"};

    public Producer(Queue queueWrapper, Queue deadQueue, QueueServiceImpl queueServiceImpl) {
        this.queue = queueWrapper;
        this.deadQueue = deadQueue;
        this.producerId = "" + Math.round(Math.random()*1000);
        this.queueServiceImpl = queueServiceImpl;
    }

    @Override
    public void run() {
        produce();
    }

    private void produce() {
        //while(produceFlag) {
        for(int i=0;i<5;i++) {
            message = getRandomMessage();
            boolean retry = true;
            while(retry && message.getRetryCount() < Message.MAX_RETRY_COUNT) {
                try {
                    while(queue.isFull()) {
                        System.out.println("Queue is full, cannot add more messages to the queue");
                        queue.waitOnFull();
                    }

                    // uncomment below line to generate exception during produce()
                    /*if(message.getMessageId().equals("200")) {
                        throw new Exception();
                    }*/


                    /*if(!produceFlag) {
                        break;
                    }*/
                    queue.add(message);
                    //System.out.println("Producer: " + producerId + " has produced message with id: " + message.getMessageId() + ", thread name" + Thread.currentThread().getName());
                    System.out.printf("New message %s added to the queue, Queue size: %d\n", message.getContent(), queue.getCurrSize());
                    queue.notifyOnEmpty();
                    retry = false;
                } catch (Exception e) {
                    System.err.println("Exception occurred in produce(): " + e);
                    message.setRetryCount(message.getRetryCount() + 1);
                    retry = true;
                }
            }
            if(message.getRetryCount() >= Message.MAX_RETRY_COUNT) {
                //System.out.println("Adding message: " + message.getMessageId() + " to dead queue");
                System.out.printf("Retries exhausted, moving messageId %s to sideline\n", message.getMessageId());
                deadQueue.add(message);
            } else {
                // start consumer
                Thread t = new Thread(queueServiceImpl);
                t.start();
                /*try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException occurred in Producer.java: " + e);
                }*/
            }
        }
    }

    private Message getRandomMessage() {
        int randomNumber = (int) ((Math.random() * (messages.length-1 - 0)) + 0);
        return new Message(messages[randomNumber]);
    }

    public void stopProducing() {
        this.produceFlag = false;
        queue.notifyOnFull();
    }
}
