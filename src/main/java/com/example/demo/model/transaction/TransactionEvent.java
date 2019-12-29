package com.example.demo.model.transaction;

public class TransactionEvent extends Transaction {
    public static enum TRANSACTION_EVENT_TYPE {
        CREATE, UPDATE
    }

    TRANSACTION_EVENT_TYPE eventType;
    String eventTimeStamp;

    public TransactionEvent(Transaction transaction, TRANSACTION_EVENT_TYPE eventType, String eventTimeStamp) {
        super(transaction);
        this.eventType = eventType;
        this.eventTimeStamp = eventTimeStamp;
    }

    public TRANSACTION_EVENT_TYPE getEventType() {
        return eventType;
    }

    public void setEventType(TRANSACTION_EVENT_TYPE eventType) {
        this.eventType = eventType;
    }

    public String getEventTimeStamp() {
        return eventTimeStamp;
    }

    public void setEventTimeStamp(String eventTimeStamp) {
        this.eventTimeStamp = eventTimeStamp;
    }
}
