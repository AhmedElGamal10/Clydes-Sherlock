package com.example.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

public class TransactionId implements Serializable {
    private String id;
    private String created;

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBRangeKey
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
