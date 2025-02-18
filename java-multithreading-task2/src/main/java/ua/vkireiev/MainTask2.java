package ua.vkireiev;

public class MainTask2 {
    public static void main(String[] args) {
        Counter counter = new Counter();
        Thread thread1 = new Thread(new Incrementer(counter));
        Thread thread2 = new Thread(new Incrementer(counter));

        System.out.println("START.  Counter = " + counter.getCounter());

        try {
            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
        }

        System.out.println("FINISH. Counter = " + counter.getCounter());
    }
}
