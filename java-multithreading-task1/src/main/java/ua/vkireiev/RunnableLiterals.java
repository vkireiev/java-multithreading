package ua.vkireiev;

public class RunnableLiterals implements Runnable {
    @Override
    public void run() {
        try {
            for (char i = 'A'; i <= 'E'; i++) {
                System.out.println(i + " ");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
        }
    }
}
