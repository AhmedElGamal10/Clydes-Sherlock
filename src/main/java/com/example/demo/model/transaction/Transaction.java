package com.example.demo.model.transaction;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.demo.model.user.UserTransactionsIndexKey;

@DynamoDBTable(tableName = "transactions")
public class Transaction {
    private final double EPS = 1e-7;

    private String id;
    private String userId;
    private String created;

    private Double amount;
    private String state;

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

//    @DynamoDBIndexHashKey(globalSecondaryIndexName = "userTransactions-index")
    public String getUserId() {
        return userId;
//        return userTransactionsIndexKey != null ? userTransactionsIndexKey.getTransactionUserId() : null;
    }

    public void setUserId(String userId) {
        this.userId = userId;
//        if (userTransactionsIndexKey == null) {
//            userTransactionsIndexKey = new UserTransactionsIndexKey();
//        }
//        userTransactionsIndexKey.setTransactionUserId(userId);
    }

//    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "userTransactions-index")
    public String getCreated() {
        return created;
//        return userTransactionsIndexKey != null ? userTransactionsIndexKey.getCreatedDate() : null;
    }

    public void setCreated(String created) {
        this.created = created;
//        if (userTransactionsIndexKey == null) {
//            userTransactionsIndexKey = new UserTransactionsIndexKey();
//        }
//        userTransactionsIndexKey.setCreatedDate(created);
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

//    @Override
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
