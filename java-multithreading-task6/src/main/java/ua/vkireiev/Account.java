package ua.vkireiev;

import java.util.Objects;

public class Account {
    final long id;
    volatile long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    synchronized boolean deposit(long amount) {
        this.balance += amount;

        notifyAll();
        return true;
    }

    synchronized boolean withdraw(long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;

            notifyAll();
            return true;
        } else {
            notifyAll();
            throw new InsufficientFundsException("Insufficient funds");
        }
    }

    synchronized boolean transfer(Account toAccount, long amount) {
        boolean result = false;
        long fromBalance = this.balance;
        try {
            synchronized (toAccount) {
                try {
                    boolean depositResult = false;
                    boolean withdrawResult = this.withdraw(amount);
                    if (withdrawResult) {
                        depositResult = toAccount.deposit(amount);
                    }

                    result = withdrawResult && depositResult;
                    if (!result && withdrawResult) {
                        toAccount.deposit(amount);
                    }
                } finally {
                    toAccount.notifyAll();
                }
            }
        } catch (InsufficientFundsException e) {
            this.balance = fromBalance;
            throw e;
        } finally {
            notifyAll();
        }

        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account that = (Account) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
