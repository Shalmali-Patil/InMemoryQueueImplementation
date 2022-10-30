package com.assignment.inmemoryqueue.model;

import com.assignment.inmemoryqueue.service.QueueServiceImpl;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class Producer implements Runnable {
    private Queue queue;
    private final String producerId;
    private boolean produceFlag = true;
    private Message message;
    private QueueServiceImpl queueServiceImpl;

    private Queue deadQueue;
    private final String[] messages = new String[]{"200","400","400","200","200","400"};

    private ObjectMapper objectMapper;

    public Producer(Queue queueWrapper, Queue deadQueue, QueueServiceImpl queueServiceImpl) {
        this.queue = queueWrapper;
        this.deadQueue = deadQueue;
        this.producerId = "" + Math.round(Math.random()*1000);
        this.queueServiceImpl = queueServiceImpl;
        this.objectMapper = new ObjectMapper();
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
                    String messageStr = null;
                    try {
                        messageStr = objectMapper.writeValueAsString(message);
                    } catch (IOException e) {
                        System.out.println("Error occurred while convering object to json string. Exception: " + e);
                    }
                    queue.add(messageStr);
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
                System.out.printf("Retries exhausted, moving messageId %s to sideline\n", message.getMessageId());
                String messageStr = null;
                try {
                    messageStr = objectMapper.writeValueAsString(message);
                } catch (IOException ioException) {
                    System.out.println("Error occurred while convering object to json string. Exception: " + ioException);
                }
                deadQueue.add(messageStr);
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
        String randomMessageId = getMessageId();
        String randomHttpStatus = messages[randomNumber];
        MessageContent content = new MessageContent(randomMessageId, randomHttpStatus);
        return new Message(randomMessageId, content);
    }

    private String getMessageId() {
        String possibleChars = "abcdefghijklmnopqrstuvxyz";
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<3;i++) {
            int index = (int) (Math.random() * possibleChars.length());
            sb.append(possibleChars.charAt(index));
        }
        return sb.toString();
    }
    public void stopProducing() {
        this.produceFlag = false;
        queue.notifyOnFull();
    }
}
