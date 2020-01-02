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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

//    @Autowired
//    private AmazonDynamoDBAsync amazonDynamoDBAsync;

    @PostConstruct
    private void initializeDatabase(){
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest createTableRequest = buildTransactionsTableCreateRequest();
//        CreateTableRequest createTableRequest = dynamoDBMapper
//                .generateCreateTableRequest(Transaction.class);

//        DeleteTableRequest deleteTableRequest = dynamoDBMapper
//                .generateDeleteTableRequest(Transaction.class);
//        TableUtils.deleteTableIfExists(amazonDynamoDB, deleteTableRequest);

//        createTableRequest.setProvisionedThroughput(
//                new ProvisionedThroughput(10L, 10L));
//        createTableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

//        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
        DeleteTableRequest d = new DeleteTableRequest();
        d.setTableName("transactions");
        amazonDynamoDB.deleteTable(d);
        amazonDynamoDB.createTable(createTableRequest);
    }

    private CreateTableRequest buildTransactionsTableCreateRequest() {

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("userId")
                .withAttributeType("S"));

        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("created")
                .withAttributeType("S"));

        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("id")
                .withAttributeType("S"));


        // Key schema of the table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement()
                .withAttributeName("id")
                .withKeyType(KeyType.HASH));              //Partition key


        // Wind index
        GlobalSecondaryIndex userTransactionsIndex = new GlobalSecondaryIndex()
                .withIndexName("userTransactions-index")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("userId")
                .withKeyType(KeyType.HASH));              //Partition key

        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("created")
                .withKeyType(KeyType.RANGE));             //Sort key

        userTransactionsIndex.setKeySchema(indexKeySchema);

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName("transactions")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 5)
                        .withWriteCapacityUnits((long) 1))
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema)
                .withGlobalSecondaryIndexes(userTransactionsIndex);

        return createTableRequest;
    }

    public PutItemResult save(Transaction transaction) {

        PutItemRequest putItemRequest = new PutItemRequest();
        putItemRequest.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        putItemRequest.setReturnValues(ReturnValue.ALL_OLD);

        Map<String,AttributeValue> attributeValues = new HashMap<>();

        attributeValues.put("userId",new AttributeValue().withS(transaction.getUserId()));
        attributeValues.put("created",new AttributeValue().withS(transaction.getCreated()));
        attributeValues.put("id",new AttributeValue().withS(transaction.getId()));
        attributeValues.put("state",new AttributeValue().withS(transaction.getState()));
        attributeValues.put("amount",new AttributeValue().withN((String.valueOf(transaction.getAmount()))));

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
        List<Transaction> unmarshalledQueryResult = dynamoDBMapper.marshallIntoObjects(Transaction.class, queryResult.getItems());

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
