package ua.vkireiev;

public class MainTask1 {
    public static void main(String[] args) {
        ThreadDigits digits = new ThreadDigits();
        Thread literals = new Thread(new RunnableLiterals());

        try {
            digits.start();
            Thread.sleep(10);
            literals.start();

            digits.join();
            literals.join();
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
        }
    }
}
