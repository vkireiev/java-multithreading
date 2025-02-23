package ua.vkireiev;

import java.util.Objects;
import java.util.Set;

public class Transaction {
    final private long id;
    final private TransactionType type;
    final private Account fromAccount;
    final private Account toAccount;
    final private long amount;
    final private long createdAt;
    private long processedAt;
    volatile private TransactionStatus status;
    volatile private int retryCount = 0;

    public Transaction(long id, TransactionType type, Account fromAccount, Account toAccount, long amount) {
        if (Objects.isNull(fromAccount) && type != TransactionType.DEPOSIT) {
            throw new TransactionException("Transaction.fromAccount cannot be 'null' when Transaction.type = " + type);
        }
        if (Objects.isNull(toAccount) && type != TransactionType.WITHDRAWAL) {
            throw new TransactionException("Transaction.fromAccount cannot be 'null' when Transaction.type = " + type);
        }
        if (fromAccount == toAccount) {
            throw new TransactionException("Cannot create a Transaction for yourself");
        }
        this.id = id;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.createdAt = System.currentTimeMillis();
        this.status = TransactionStatus.PENDING;
    }

    public boolean isProcessing() {
        return this.status == TransactionStatus.PROCESSING;
    }

    public boolean isSuccess() {
        return this.status == TransactionStatus.SUCCESS;
    }

    public boolean isCancelled() {
        return this.status == TransactionStatus.CANCELLED;
    }

    public Set<Account> getAccounts() {
        return switch (this.type) {
            case TRANSFER -> Set.of(this.getFromAccount(), this.getToAccount());
            case DEPOSIT -> Set.of(this.getToAccount());
            case WITHDRAWAL -> Set.of(this.getFromAccount());
        };
    }

    public long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public long getAmount() {
        return amount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getProcessedAt() {
        return processedAt;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setTransactionStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setProcessedAt(long processedAt) {
        this.processedAt = processedAt;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    synchronized void increaseRetryCount() {
        this.retryCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", fromAccount=" + fromAccount +
                ", toAccount=" + toAccount +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                ", status=" + status +
                ", retryCount=" + retryCount +
                '}';
    }
}
