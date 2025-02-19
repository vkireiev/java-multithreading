package ua.vkireiev;

public class MainTask4 {
    public static void main(String[] args) {
        System.out.println("Thread '" + Thread.currentThread().getName() + "' start");
        RunnerThread runner = new RunnerThread();
        Thread runnerThread = new Thread(runner, "Runner");
        //Thread shutdownThread = new Thread(new ShutDownThread(runner), "Shutdown");
        Thread shutdownThread = new Thread(() -> {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' start");
            try {
                Thread.sleep(5500);
                runner.shutDownMe();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
            }
            System.out.println("Thread '" + Thread.currentThread().getName() + "' finish");
        }, "Shutdown");

        try {
            runnerThread.start();
            shutdownThread.start();

            runnerThread.join();
            shutdownThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
        }
        System.out.println("Thread '" + Thread.currentThread().getName() + "' finish");
    }
}
