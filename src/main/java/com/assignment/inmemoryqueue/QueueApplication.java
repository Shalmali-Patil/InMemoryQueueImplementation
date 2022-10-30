package com.assignment.inmemoryqueue;

import com.assignment.inmemoryqueue.model.*;
import com.assignment.inmemoryqueue.service.QueueServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class QueueApplication {
    public static void main(String[] args) {

        Queue queue = new Queue(1);
        Queue deadQueue = new Queue(queue.getCapacity());
        QueueServiceImpl queueServiceImpl = new QueueServiceImpl(queue, deadQueue);

        Producer producer = new Producer(queue, deadQueue, queueServiceImpl);
        List<Thread> threads = new ArrayList<>();
        Thread prodThread = new Thread(producer);
        prodThread.start();
        threads.add(prodThread);

        String twoHundredStatusPattern = ".*\\\"httpCode\\\":\\\"200\\\".*";
        String fourHundredStatusPattern = ".*\\\"httpCode\\\":\\\"(?!200).*";
        Consumer consumer = new Consumer(queue, twoHundredStatusPattern, null, "consume", "consumer1");
        List<Consumer> dependencies = new ArrayList<>();
        Consumer1 consumer2 = new Consumer1(queue, fourHundredStatusPattern,  null, "consume1", "consumer2");
        dependencies.add(consumer2);
        Consumer consumer3 = new Consumer(queue, fourHundredStatusPattern,  dependencies, "consume", "consumer3");
        queueServiceImpl.subscribe(fourHundredStatusPattern, consumer3);
        queueServiceImpl.subscribe(fourHundredStatusPattern, consumer2);
        queueServiceImpl.subscribe(twoHundredStatusPattern, consumer);

        /*producer.stopProducing();
        consumer.stopConsuming();
        producer2.stopProducing();
        consumer2.stopConsuming();*/

        for(Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
