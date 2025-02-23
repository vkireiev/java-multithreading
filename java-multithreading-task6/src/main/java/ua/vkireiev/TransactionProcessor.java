package ua.vkireiev;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionProcessor implements Runnable {
    private static final int MAX_PROCESS_ATTEMPTS = 2;
    private static final List<Transaction> transactions = new ArrayList<>();
    private volatile static long countTransactions = 0;
    private long sleep;
    private Set<TransactionType> types;
    private Set<TransactionStatus> statuses;

    private static final int MAX_IDLE_COUNT = 25;
    private volatile int idleCount = 0;
    private static final Object transactionsLockForProcessTransaction = new Object();

    public TransactionProcessor() {
        this(Set.of(TransactionType.values()), Set.of(TransactionStatus.PENDING));
    }

    public TransactionProcessor(Set<TransactionType> types, Set<TransactionStatus> statuses) {
        if (Objects.isNull(types) || types.isEmpty()) {
            this.types = Set.of(TransactionType.values());
        } else {
            this.types = Set.copyOf(types);
        }
        if (Objects.isNull(statuses) || statuses.isEmpty()) {
            this.statuses = Set.of(TransactionStatus.PENDING);
        } else {
            this.statuses = Set.copyOf(statuses);
        }
        this.sleep = 5000;
    }

    @Override
    public void run() {
        System.out.println(getFormatedThreadName() + "start");

        while (Bank.transactionProcessing && this.idleCount < MAX_IDLE_COUNT) {
            try {
                Thread.sleep(this.sleep);
                processTransaction();
            } catch (InterruptedException e) {
                System.out.println(getFormatedThreadName() + "interrupted");
            }
        }

        System.out.println(getFormatedThreadName() + "finish");
    }

    private void processTransaction() {
        List<String> outputs = new LinkedList<>();
        Optional<Transaction> transactionOptional = Optional.empty();
        Transaction transaction = null;
        synchronized (transactionsLockForProcessTransaction) {
            Set<Account> lockedAccounts = transactions.stream()
                    .filter(Transaction::isProcessing)
                    .map(Transaction::getAccounts)
                    .flatMap(Set::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            transactionOptional = transactions.stream()
                    .filter(Predicate.not(Transaction::isSuccess))
                    .filter(Predicate.not(Transaction::isProcessing))
                    .filter(Predicate.not(Transaction::isCancelled))
                    .filter(t -> this.types.contains(t.getType()))
                    .filter(t -> this.statuses.contains(t.getStatus()))
                    .filter(t -> !lockedAccounts.contains(t.getFromAccount()))
                    .filter(t -> !lockedAccounts.contains(t.getToAccount()))
                    .findAny();
            if (transactionOptional.isPresent()) {
                transaction = transactionOptional.get();
                transaction.setTransactionStatus(TransactionStatus.PROCESSING);
            }
        }
        if (transactionOptional.isPresent()) {
            this.idleCount = 0;
            synchronized (transaction) {
                outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                        + "| trying to process Transaction ID=" + transaction.getId());
                transaction.setTransactionStatus(TransactionStatus.PROCESSING);
                Account fromAccount = transaction.getFromAccount();
                Account toAccount = transaction.getToAccount();
                long amount = transaction.getAmount();
                outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                        + "| " + transaction);
                outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                        + "| amount => " + amount);
                outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                        + "| fromAccount => " + fromAccount
                        + ", toAccount => " + toAccount);
                try {
                    boolean result = switch (transaction.getType()) {
                        case TRANSFER -> fromAccount.transfer(toAccount, amount);
                        case DEPOSIT -> toAccount.deposit(amount);
                        case WITHDRAWAL -> fromAccount.withdraw(amount);
                    };

                    transaction.setProcessedAt(System.currentTimeMillis());
                    transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                    outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                            + "| Transaction ID=" + transaction.getId() + " processed");
                    outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                            + "| fromAccount => " + fromAccount
                            + ", toAccount => " + toAccount);
                    outputs.add(getFormatedThreadName() + "|ID=" + transaction.getId()
                            + "| " + transaction);
                } catch (InsufficientFundsException e) {
                    transaction.increaseRetryCount();
                    if (transaction.getRetryCount() > MAX_PROCESS_ATTEMPTS) {
                        transaction.setTransactionStatus(TransactionStatus.CANCELLED);
                    } else {
                        transaction.setTransactionStatus(TransactionStatus.FAILED);
                    }
                    outputs.add("\u001B[31m" + getFormatedThreadName() + "|ID=" + transaction.getId()
                            + "| Transaction ID=" + transaction.getId()
                            + " failed processing (" + e.getMessage() + ")" + "\u001B[0m");
                    outputs.add("\u001B[31m" + getFormatedThreadName() + "|ID=" + transaction.getId()
                            + "| " + transaction + "\u001B[0m");
                } finally {
                    transaction.notifyAll();
                    synchronized (System.out) {
                        outputs.forEach(System.out::println);
                    }
                }
            }
        } else {
            System.out.println(getFormatedThreadName() + "nothing to process... ("
                    + (this.idleCount++) + "/" + MAX_IDLE_COUNT + ")");
        }
    }

    static Transaction addTransaction(TransactionType type, Account fromAccount, Account toAccount, long amount) {
        synchronized (transactions) {
            Transaction transaction = switch (type) {
                case DEPOSIT -> new Transaction(
                        countTransactions++,
                        TransactionType.DEPOSIT,
                        null,
                        toAccount,
                        amount
                );
                case WITHDRAWAL -> new Transaction(
                        countTransactions++,
                        TransactionType.WITHDRAWAL,
                        fromAccount,
                        null,
                        amount
                );
                case TRANSFER -> new Transaction(
                        countTransactions++,
                        TransactionType.TRANSFER,
                        fromAccount,
                        toAccount,
                        amount
                );
            };
            transactions.add(transaction);

            transactions.notifyAll();
            return transaction;
        }
    }

    static void printTransactions() {
        transactions.forEach(System.out::println);
    }

    public static class Builder {
        private final TransactionProcessor newTransactionProcessor;

        public Builder() {
            newTransactionProcessor = new TransactionProcessor();
        }

        public Builder withTransactionTypes(Set<TransactionType> types) {
            if (Objects.isNull(types) || types.isEmpty()) {
                newTransactionProcessor.types = Set.of(TransactionType.values());
            } else {
                newTransactionProcessor.types = Set.copyOf(types);
            }
            return this;
        }

        public Builder withTransactionStatus(Set<TransactionStatus> statuses) {
            if (Objects.isNull(statuses) || statuses.isEmpty()) {
                newTransactionProcessor.statuses = Set.of(TransactionStatus.PENDING);
            } else {
                newTransactionProcessor.statuses = Set.copyOf(statuses);
            }
            return this;
        }

        public Builder withSleep(long sleep) {
            newTransactionProcessor.sleep = sleep;
            return this;
        }

        public TransactionProcessor build() {
            return newTransactionProcessor;
        }
    }


    private static String getFormatedThreadName() {
        return String.format("%-25s", "Thread '" + Thread.currentThread().getName() + "'") + ": ";
    }
}
