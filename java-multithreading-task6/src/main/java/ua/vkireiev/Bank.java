package ua.vkireiev;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Bank {
    static final private int START_ACCOUNTS_COUNT = 5;
    static final private Bank self = new Bank();
    static final private List<Account> accounts = new ArrayList<>();
    volatile static private long countAccounts = 0;
    volatile static boolean transactionGenerating = true;
    volatile static boolean transactionProcessing = true;

    static {
        for (int i = 0; i < START_ACCOUNTS_COUNT; i++) {
            Bank.addAccount(ThreadLocalRandom.current().nextInt(0, 501));
        }
    }

    private Bank() {
    }

    static Bank getInstance() {
        return self;
    }

    static boolean addAccount(long balance) {
        synchronized (accounts) {
            boolean result = false;
            try {
                Account account = new Account(
                        countAccounts++,
                        balance);
                result = accounts.add(account);
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            accounts.notifyAll();
            return result;
        }
    }


    static void printAccounts() {
        accounts.forEach(System.out::println);
    }

    public static Optional<Account> getRandomAccount() {
        if (accounts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(accounts.get(ThreadLocalRandom.current().nextInt(accounts.size())));
    }
}
