package ua.vkireiev;

public class RunnerThread implements Runnable {
    private volatile boolean isRunning = true;

    @Override
    public void run() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "' start");
        while (isRunning) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "': I am still running...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
            }
        }

        System.out.println("Thread '" + Thread.currentThread().getName() + "': I am shutting down...");
        System.out.println("Thread '" + Thread.currentThread().getName() + "' finish");
    }

    synchronized public void shutDownMe() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "': Trying to shut down...");
        this.isRunning = false;
    }
}
