package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CustomTransactionRepositoryImpl implements CustomTransactionRepository {
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

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

//    private void dummyFun() {
//        DynamoDbAsyncClient client = DynamoDbAsyncClient.create();
//        CompletableFuture<ListTablesResponse> response = client.listTables(ListTablesRequest.builder()
//                .build());
//
//        // Map the response to another CompletableFuture containing just the table names
//        CompletableFuture<List<String>> tableNames = response.thenApply(ListTablesResponse::tableNames);
//        // When future is complete (either successfully or in error) handle the response
//        tableNames.whenComplete((tables, err) -> {
//            try {
//                if (tables != null) {
//                    tables.forEach(System.out::println);
//                } else {
//                    // Handle error
//                    err.printStackTrace();
//                }
//            } finally {
//                // Lets the application shut down. Only close the client when you are completely done with it.
//                client.close();
//            }
//        });
//
//    }

    @Override
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

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);

        List<Transaction> unmarshalledQueryResult = dynamoDBMapper.marshallIntoObjects(Transaction.class, queryResult.getItems());

        return unmarshalledQueryResult;
    }
}
