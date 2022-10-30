package com.assignment;

import com.assignment.model.*;
import com.assignment.service.QueueServiceImpl;

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

        Consumer consumer = new Consumer(queue, "200", null, "consume", "consumer1");
        List<Consumer> dependencies = new ArrayList<>();
        Consumer1 consumer2 = new Consumer1(queue, "400",  null, "consume1", "consumer2");
        dependencies.add(consumer2);
        Consumer consumer3 = new Consumer(queue, "400",  dependencies, "consume", "consumer3");
        queueServiceImpl.subscribe("400", consumer3);
        queueServiceImpl.subscribe("400", consumer2);
        queueServiceImpl.subscribe("200", consumer);

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
