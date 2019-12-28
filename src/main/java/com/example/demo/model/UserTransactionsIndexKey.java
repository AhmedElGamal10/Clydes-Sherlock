package com.example.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

public class UserTransactionsIndexKey implements Serializable {
    private String userId;
    private String created;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "userTransactions-index")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "userTransactions-index")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
