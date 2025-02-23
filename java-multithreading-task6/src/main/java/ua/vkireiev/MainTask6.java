package ua.vkireiev;

import java.util.Set;

public class MainTask6 {
    public static void main(String[] args) {
        System.out.println("Thread '" + Thread.currentThread().getName() + "': start");

        TransactionGenerator transactionGenerator1 = new TransactionGenerator();
        TransactionGenerator transactionGenerator2 = new TransactionGenerator();
        Thread transactionGeneratorThread1 = new Thread(transactionGenerator1, "TG1");
        Thread transactionGeneratorThread2 = new Thread(transactionGenerator2, "TG2");
        TransactionProcessor transactionProcessor1 = new TransactionProcessor.Builder()
                .withTransactionTypes(Set.of(TransactionType.DEPOSIT))
                .withTransactionStatus(Set.of(TransactionStatus.PENDING))
                .withSleep(10000)
                .build();
        Thread transactionProcessorThread1 = new Thread(transactionProcessor1, "TP1_DEPOSIT");
        TransactionProcessor transactionProcessor2 = new TransactionProcessor.Builder()
                .withTransactionTypes(Set.of(TransactionType.WITHDRAWAL))
                .withTransactionStatus(Set.of(TransactionStatus.PENDING))
                .withSleep(10000)
                .build();
        Thread transactionProcessorThread2 = new Thread(transactionProcessor2, "TP2_WITHDRAWAL");
        TransactionProcessor transactionProcessor3 = new TransactionProcessor.Builder()
                .withTransactionTypes(Set.of(TransactionType.TRANSFER))
                .withTransactionStatus(Set.of(TransactionStatus.PENDING))
                .build();
        Thread transactionProcessorThread3 = new Thread(transactionProcessor3, "TP3_TRANSFER");
        Thread transactionProcessorThread4 = new Thread(new TransactionProcessor.Builder()
                .withTransactionStatus(Set.of(TransactionStatus.FAILED))
                .withSleep(30000)
                .build(), "TP4_FAILED");

        try {
            transactionGeneratorThread1.start();
            transactionGeneratorThread2.start();
            Thread.sleep(50000);
            transactionProcessorThread1.start();
            transactionProcessorThread2.start();
            transactionProcessorThread3.start();
            transactionProcessorThread4.start();

            transactionGeneratorThread1.join();
            transactionGeneratorThread2.join();
            transactionProcessorThread1.join();
            transactionProcessorThread2.join();
            transactionProcessorThread3.join();
            transactionProcessorThread4.join();
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "': interrupted");
        }

        System.out.println("Thread '" + Thread.currentThread().getName() + "': finish");
    }
}
