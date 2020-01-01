package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private AmazonDynamoDBAsync amazonDynamoDBAsync;

    @PostConstruct
    private void initializeDatabase(){
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest createTableRequest = dynamoDBMapper
                .generateCreateTableRequest(Transaction.class);

        DeleteTableRequest deleteTableRequest = dynamoDBMapper
                .generateDeleteTableRequest(Transaction.class);
        TableUtils.deleteTableIfExists(amazonDynamoDB, deleteTableRequest);

        createTableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(10L, 10L));
        createTableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
    }

//    @PostConstruct
//    private void initializeDatabase(){
//        CreateTableRequest createTableRequest = dynamoDBMapper
//                .generateCreateTableRequest(Transaction.class);
//
////        DeleteTableRequest deleteTableRequest = dynamoDBMapper
////                .generateDeleteTableRequest(Transaction.class);
////        TableUtils.deleteTableIfExists(amazonDynamoDBAsync, deleteTableRequest);
//
//        createTableRequest.setProvisionedThroughput(
//                new ProvisionedThroughput(10L, 10L));
//        createTableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
//
//        TableUtils.createTableIfNotExists(amazonDynamoDBAsync, createTableRequest);
//    }

//    @Override
//    public Future<PutItemResult> save(Transaction transaction) {
//        Map<String,AttributeValue> attributeValues = new HashMap<>();
//
//        attributeValues.put("userId",new AttributeValue().withS(transaction.getUserId()));
//        attributeValues.put("created",new AttributeValue().withS(transaction.getCreated()));
//        attributeValues.put("id",new AttributeValue().withS(transaction.getId()));
//        attributeValues.put("state",new AttributeValue().withS(transaction.getState()));
//        // TODO
//        attributeValues.put("amount",new AttributeValue().withN(String.valueOf(transaction.getAmount())));
//
//        PutItemRequest putItemRequest = new PutItemRequest();
//        putItemRequest.withTableName("transactions");
//        putItemRequest.withItem(attributeValues);
//
//        Future<PutItemResult> putItemResult = amazonDynamoDBAsync.putItemAsync(putItemRequest);
//        try {
//            Map<String, AttributeValue> temp =  putItemResult.get().getAttributes();
//            System.out.println();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return putItemResult;
//    }

    public PutItemResult save(Transaction transaction) {
        Map<String,AttributeValue> attributeValues = new HashMap<>();

        attributeValues.put("userId",new AttributeValue().withS(transaction.getUserId()));
        attributeValues.put("created",new AttributeValue().withS(transaction.getCreated()));
        attributeValues.put("id",new AttributeValue().withS(transaction.getId()));
        attributeValues.put("state",new AttributeValue().withS(transaction.getState()));
        // TODO
        attributeValues.put("amount",new AttributeValue().withN(String.valueOf(transaction.getAmount())));

        PutItemRequest putItemRequest = new PutItemRequest();
        putItemRequest.withTableName("transactions");
        putItemRequest.withItem(attributeValues);

        return amazonDynamoDB.putItem(putItemRequest);
    }

//    @Override
//    public List<Transaction> queryUserTransactionsIndex(User user, String startDate, String endDate) {
//        Map<String, String> expressionAttributesNames = new HashMap<>();
//        expressionAttributesNames.put("#userId", "userId");
//        expressionAttributesNames.put("#created", "created");
//
//        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
//        expressionAttributeValues.put(":userId", new AttributeValue().withS(user.getId()));
//        expressionAttributeValues.put(":currentDate", new AttributeValue().withS(endDate));
//        expressionAttributeValues.put(":fiveDaysAgoDate", new AttributeValue().withS(startDate));
//
//        QueryRequest queryRequest = new QueryRequest()
//                .withTableName("transactions")
//                .withKeyConditionExpression("#userId = :userId and #created between :fiveDaysAgoDate and :currentDate ")
//                .withIndexName("userTransactions-index")
//                .withExpressionAttributeNames(expressionAttributesNames)
//                .withExpressionAttributeValues(expressionAttributeValues);
//
//        Future<QueryResult> queryResult = amazonDynamoDBAsync.queryAsync(queryRequest);
//
//        List<Transaction> unmarshalledQueryResult = dynamoDBMapper.marshallIntoObjects(Transaction.class, queryResult);
//
//        return unmarshalledQueryResult;
//    }

//    @Override
    public List<Transaction> queryUserTransactionsIndex(User user, String startDate, String endDate) {
        Map<String, String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#userId", "userId");
        expressionAttributesNames.put("#created", "created");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userId", new AttributeValue().withS(user.getId()));
        expressionAttributeValues.put(":currentDate", new AttributeValue().withS(endDate));
        expressionAttributeValues.put(":fiveDaysAgoDate", new AttributeValue().withS(startDate));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("transactions")
                .withKeyConditionExpression("#userId = :userId and #created between :fiveDaysAgoDate and :currentDate ")
                .withIndexName("userTransactions-index")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues);

//        Future<QueryResult> queryResult = amazonDynamoDBAsync.queryAsync(queryRequest);
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Transaction> unmarshalledQueryResult = unmarshalledQueryResult = dynamoDBMapper.marshallIntoObjects(Transaction.class, queryResult.getItems());

//        try {
//            unmarshalledQueryResult = dynamoDBMapper.marshallIntoObjects(Transaction.class, queryResult.get().getItems());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        return unmarshalledQueryResult;
    }
}
