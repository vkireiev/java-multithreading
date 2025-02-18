package ua.vkireiev;

public class ThreadDigits extends Thread {
    @Override
    public void run() {
        try {
            for (int i = 1; i <= 5; i++) {
                System.out.print(i + " ");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
        }
    }
}
