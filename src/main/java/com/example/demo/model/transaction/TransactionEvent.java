package com.example.demo.model.transaction;

public class TransactionEvent {
    public static enum TRANSACTION_EVENT_TYPE {
        CREATE, UPDATE
    }

    Transaction transaction;
    String eventTimeStamp;
    TRANSACTION_EVENT_TYPE eventType;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public TransactionEvent(Transaction transaction, TRANSACTION_EVENT_TYPE eventType, String eventTimeStamp) {
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

    public TRANSACTION_EVENT_TYPE getEventType() {
        return eventType;
    }
    public void setEventType(TRANSACTION_EVENT_TYPE eventType) {
        this.eventType = eventType;
    }

    public void setUserId(String userId) {
        this.transaction.setUserId(userId);
    }

    @Override
    public String toString() {
        return "Transaction [userId=" + transaction.getUserId() + ", id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", state=" + transaction.getState() + ", created=" + transaction.getCreated() + ", eventType=" + eventType + ", eventTimeStamp=" + eventTimeStamp +"]";
    }
}
