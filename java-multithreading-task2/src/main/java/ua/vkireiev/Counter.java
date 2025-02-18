package ua.vkireiev;

public class Counter {
    private int counter = 0;

    public synchronized void increment() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "' incrementing Counter...");
        this.counter++;
    }

    public int getCounter() {
        return counter;
    }
}
