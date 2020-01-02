package com.example.demo.model.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;

import java.io.Serializable;

public class UserTransactionsIndexKey implements Serializable {
    private String transactionUserId;
    private String createdDate;

//    @DynamoDBIndexHashKey(globalSecondaryIndexName = "userTransactions-index")
    public String getTransactionUserId() {
        return transactionUserId;
    }
    public void setTransactionUserId(String transactionUserId) {
        this.transactionUserId = transactionUserId;
    }

//    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "userTransactions-index")
    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
