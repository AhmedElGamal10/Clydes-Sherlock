package com.clyde.sherlock.model.transaction;

import com.clyde.sherlock.util.ServiceProvider;

public class TransactionEvent {
    public enum TransactionEventType {
        CREATE, UPDATE
    }

    Transaction transaction;
    String eventTimeStamp;
    TransactionEventType eventType;
    private static final ServiceProvider SERVICE_PROVIDER = ServiceProvider.CLYDE;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public TransactionEvent(Transaction transaction, TransactionEventType eventType, String eventTimeStamp) {
        this.transaction = transaction;
        this.eventType = eventType;
        this.eventTimeStamp = eventTimeStamp;
    }

    public String getEventTimeStamp() {
        return eventTimeStamp;
    }

    public void setEventTimeStamp(String eventTimeStamp) {
        this.eventTimeStamp = eventTimeStamp;
    }

    public TransactionEventType getEventType() {
        return eventType;
    }

    public void setEventType(TransactionEventType eventType) {
        this.eventType = eventType;
    }

    public void setUserId(String userId) {
        this.transaction.setUserId(userId);
    }

    @Override
    public String toString() {
        return "Transaction [userId=" + transaction.getUserId() + ", id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", state=" + transaction.getState() + ", created=" + transaction.getCreated() + ", eventType=" + eventType + ", eventTimeStamp=" + eventTimeStamp + ", serviceProvider=" + SERVICE_PROVIDER + "]";
    }
}
