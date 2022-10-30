package com.assignment.service;

import com.assignment.model.Consumer;
import com.assignment.model.Message;
import com.assignment.model.Queue;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueServiceImpl implements Runnable, QueueService {
    private ConcurrentHashMap<String, List<Consumer>> consumerMap = new ConcurrentHashMap<>();
    private Set<Consumer> consumerSet = new HashSet<>();
    private Queue queue;
    private Queue deadQueue;

    public QueueServiceImpl(Queue queue, Queue deadQueue) {
        this.queue = queue;
        this.deadQueue = deadQueue;
    }

    public void subscribe(String pattern, Consumer consumer) {
        List<Consumer> consumers = null;
        if(consumerMap.containsKey(pattern)) {
            consumers = consumerMap.get(pattern);
        } else {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        consumerMap.put(pattern, consumers);
        consumerSet.add(consumer);
    }

    public void consume() {
            if(queue.isEmpty()) {
                try {
                    queue.waitOnEmpty();
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException in QueueServiceImpl.java" + e);
                }
            }

            final Message m = queue.remove();

            if(m != null) {
                System.out.println("Fetched from queue: " + m.getMessageId());
                Long currTimeMillis = System.currentTimeMillis();
                if(currTimeMillis - m.getCreatedAt() > Queue.TTL) {
                    System.out.printf("\nmessageId %s is expired", m.getMessageId());
                } else {
                    if(consumerMap.get(m.getMessageId()) == null) {
                        if(m.getRetryCount() < Message.MAX_RETRY_COUNT) {
                            m.setRetryCount(m.getRetryCount()+1);
                            queue.add(m);
                            System.out.println("Added back to queue: " + m.getMessageId());
                        } else {
                            System.out.println("MAX_RETRY_COUNT reached for message: " + m.getMessageId() + "");
                        }
                    } else {
                        List<Consumer> subscribedConsumers = consumerMap.get(m.getMessageId());
                        System.out.println("For message: " + m.getMessageId() + ", subscribedConsumers: " + subscribedConsumers);
                        Set<Consumer> consumerSetCopy = new HashSet<>(consumerSet);
                        for(Consumer c: subscribedConsumers) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (consumerSetCopy) {
                                        boolean retry = true;
                                        while(retry && m.getRetryCount() < Message.MAX_RETRY_COUNT) {
                                            try {
                                                consumeMessage(m, c, consumerSetCopy);
                                                retry = false;
                                            } catch (Exception e) {
                                                System.out.println("Retrying message: " + m.getMessageId());
                                                m.setRetryCount(m.getRetryCount() + 1);
                                                retry = true;
                                            }
                                        }
                                        if(m.getRetryCount() >= Message.MAX_RETRY_COUNT) {
                                            System.out.printf("Retries exhausted, moving messageId %s to sideline\n", m.getMessageId());
                                            deadQueue.add(m);
                                            consumerSetCopy.remove(c);
                                            consumerSetCopy.notifyAll();
                                        }
                                    }
                                }
                            }).start();
                        }
                    }
                }
            }
            queue.notifyOnFull();
    }

    private void consumeMessage(Message m, Consumer c, Set<Consumer> consumerSetCopy) throws Exception {
        synchronized (consumerSetCopy) {
            List<Consumer> dependencies = c.getDependencies();
            if(dependencies != null && !dependencies.isEmpty()) {
                System.out.println("Consumer: " + c.getConsumerId() + " has dependencies: " + dependencies + ", consumerSetCopy:" + consumerSetCopy);
                for(Consumer dependency : dependencies) {
                    while(consumerSetCopy.contains(dependency)) {
                        try {
                            System.out.println("Consumer: " + c.getConsumerId() + " waiting for Consumer: " + dependency.getConsumerId());
                            consumerSetCopy.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            String callbackMethod = c.getCallbackMethod();
            try {
                c.getClass().getMethod(callbackMethod, Message.class).invoke(c, m);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                System.out.println("Exception occurred for method: " + callbackMethod +", exception is:" + e);
                throw new Exception();
            }
            consumerSetCopy.remove(c);
            consumerSetCopy.notifyAll();
        }
    }

    @Override
    public void run() {
        consume();
    }
}
