package com.example.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;

@DynamoDBTable(tableName = "transactions")
public class Transaction {
    @Id
    private TransactionId transactionId;

    private Double amount;
    private String userId;
    private String state;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "userTransactions-index")
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }


    @DynamoDBHashKey
    public String getId() {
        return transactionId != null ? transactionId.getId() : null;
    }
    public void setId(String id) {
        if (transactionId == null) {
            transactionId = new TransactionId();
        }
        transactionId.setId(id);
    }

    @DynamoDBRangeKey
    public String getCreated() {
        return transactionId != null ? transactionId.getCreated() : null;
    }
    public void setCreated(String created) {
        if (transactionId == null) {
            transactionId = new TransactionId();
        }
        transactionId.setCreated(created);
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
}
