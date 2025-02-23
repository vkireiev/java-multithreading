package ua.vkireiev;

import java.util.concurrent.ThreadLocalRandom;

public class TransactionGenerator implements Runnable {
    private volatile int count = 0;

    @Override
    public void run() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "': start");

        while (Bank.transactionGenerating && this.count < 50) {
            try {
                long sleep = ThreadLocalRandom.current().nextInt(1000, 20000);
                Thread.sleep(sleep);
                Account fromAccount = Bank.getRandomAccount().orElse(null);
                Account toAccount = Bank.getRandomAccount().orElse(null);
                TransactionType type = TransactionType
                        .values()[ThreadLocalRandom.current().nextInt(TransactionType.values().length)];
                long amount = ThreadLocalRandom.current().nextInt(10, 501);
                Transaction transaction = TransactionProcessor.addTransaction(type, fromAccount, toAccount, amount);
                System.out.println("Thread '" + Thread.currentThread().getName() + "': added " + transaction);
                this.count++;
            } catch (InterruptedException ex1) {
                System.out.println("Thread '" + Thread.currentThread().getName() + "': interrupted");
            } catch (Exception ex2) {
                System.out.println("Thread '" + Thread.currentThread().getName() + "': failed to create Transaction");
                ex2.printStackTrace();
            }
        }

        System.out.println("Thread '" + Thread.currentThread().getName() + "': finish");
    }
}
