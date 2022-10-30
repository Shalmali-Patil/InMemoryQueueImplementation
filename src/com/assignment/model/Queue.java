package com.assignment.model;

public class Queue {

    public static final long TTL = 50; // TTL in millis
    private int capacity;
    private Node head;
    private Node tail;
    private int currSize = 0;
    private Object full = new Object();
    private Object empty = new Object();

    public Queue(int capacity) {
        this.capacity = capacity;
    }

    private void addToQueue(Message message) {
        Node newNode = new Node(message);
        if(head == null && tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        currSize++;
    }

    private Message peekQueue() {
        if(head == null) {
            return null;
        }
        return head.getMessage();
    }
    private Message getFromQueue() {
        if(head == null) {
            return null;
        }
        Node newNode = head;
        if(head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.next;
            if(head != null) {
                head.prev = null;
            }
        }
        currSize--;
        return newNode.getMessage();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public int getCurrSize() {
        return currSize;
    }

    class Node {
        private Message message;
        private Node prev;
        private Node next;

        public Node(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

    public void add(Message message) {
        synchronized (this) {
            this.addToQueue(message);
        }
    }

    public Message remove() {
        synchronized (this) {
            return this.getFromQueue();
        }
    }

    public Message peek() {
        synchronized (this) {
            return this.peekQueue();
        }
    }

    public boolean isFull(){
        if(this.getCurrSize() == this.getCapacity()) {
            return true;
        }
        return false;
    }

    public boolean isEmpty(){
        if(this.getCurrSize() == 0) {
            return true;
        }
        return false;
    }

    public void waitOnFull() throws InterruptedException {
        synchronized (full) {
            full.wait();
        }
    }

    public void notifyOnFull() {
        synchronized (full) {
            full.notifyAll();
        }
    }

    public void waitOnEmpty() throws InterruptedException {
        synchronized (empty) {
            empty.wait();
        }
    }

    public void notifyOnEmpty() {
        synchronized (empty) {
            empty.notifyAll();
        }
    }
}
