package com.example.demo.model.transaction;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "transactions")
public class Transaction {
    private final double EPS = 1e-7;

    private String id;
    private String userId;
    private String created;
    private String state;
    private Double amount;

    public Transaction() {
    }

    public Transaction(Transaction transaction) {
        this.amount = transaction.getAmount();
        this.state = transaction.getState();
        this.id = transaction.getId();
        this.userId = (transaction.getUserId());
        this.created = (transaction.getCreated());
    }

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @DynamoDBAttribute(attributeName = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @DynamoDBAttribute(attributeName = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof Transaction)) {
            return false;
        }

        Transaction transaction = (Transaction) o;

        return this.getCreated().equals(transaction.getCreated()) &&
                this.getState().equals(transaction.getState()) &&
                Math.abs(this.getAmount() - transaction.getAmount()) < EPS;
    }

    @Override
    public String toString() {
        return "Transaction [userId=" + getUserId() + ", id=" + getId() + ", amount=" + getAmount() + ", state=" + getState() + ", created=" + getCreated() + "]";
    }
}
