package com.example.demo.model.transaction;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.demo.model.user.UserTransactionsIndexKey;
import org.springframework.data.annotation.Id;

@DynamoDBTable(tableName = "transactions")
public class Transaction {
    private final double EPS = 1e-7;
    private UserTransactionsIndexKey userTransactionsIndexKey;

    @Id
    private String id;

    private Double amount;

    private String state;

    public Transaction() {}
    public Transaction(Transaction transaction) {
        this.amount = transaction.getAmount();
        this.state = transaction.getState();
        this.id = transaction.getId();
        this.setUserId(transaction.getUserId());
        this.setCreated(transaction.getCreated());
    }

    @DynamoDBHashKey
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "userTransactions-index")
    public String getUserId() {
        return userTransactionsIndexKey != null ? userTransactionsIndexKey.getUserId() : null;
    }
    public void setUserId(String userId) {
        if (userTransactionsIndexKey == null) {
            userTransactionsIndexKey = new UserTransactionsIndexKey();
        }
        userTransactionsIndexKey.setUserId(userId);
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "userTransactions-index")
    public String getCreated() {
        return userTransactionsIndexKey != null ? userTransactionsIndexKey.getCreated() : null;
    }
    public void setCreated(String created) {
        if (userTransactionsIndexKey == null) {
            userTransactionsIndexKey = new UserTransactionsIndexKey();
        }
        userTransactionsIndexKey.setCreated(created);
    }

    @DynamoDBAttribute
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    @DynamoDBAttribute
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
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
}
