package ua.vkireiev;

public class Incrementer implements Runnable {
    final Counter counter;
    
    public Incrementer(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "' started...");
        for (int i = 0; i < 10; i++) {
            this.counter.increment();
        }
        System.out.println("Thread '" + Thread.currentThread().getName() + "' finished...");
    }
}
