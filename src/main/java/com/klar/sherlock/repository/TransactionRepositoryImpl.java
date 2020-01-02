package com.klar.sherlock.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.user.User;
import com.klar.sherlock.util.CompletablePromise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDBAsync amazonDynamoDBAsync;

    @PostConstruct
    private void initializeDatabase() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDBAsync);
        CreateTableRequest createTableRequest = buildTransactionsTableCreateRequest();
        TableUtils.createTableIfNotExists(amazonDynamoDBAsync, createTableRequest);
    }

    public CompletableFuture<PutItemResult> saveTransaction(Transaction transaction) {

        PutItemRequest putItemRequest = new PutItemRequest();
        putItemRequest.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        putItemRequest.setReturnValues(ReturnValue.ALL_OLD);

        Map<String, AttributeValue> attributeValues = new HashMap<>();

        attributeValues.put("userId", new AttributeValue().withS(transaction.getUserId()));
        attributeValues.put("created", new AttributeValue().withS(transaction.getCreated()));
        attributeValues.put("id", new AttributeValue().withS(transaction.getId()));
        attributeValues.put("state", new AttributeValue().withS(transaction.getState()));
        attributeValues.put("amount", new AttributeValue().withN((String.valueOf(transaction.getAmount()))));

        putItemRequest.withTableName("transactions");
        putItemRequest.withItem(attributeValues);

        return new CompletablePromise<>(amazonDynamoDBAsync.putItemAsync(putItemRequest));
    }


    public CompletableFuture<List<Transaction>> queryUserTransactionsIndex(User user, String startDate, String endDate) {
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

        return new CompletablePromise<>(amazonDynamoDBAsync.queryAsync(queryRequest)).thenApplyAsync(queryResult ->
                dynamoDBMapper.marshallIntoObjects(Transaction.class, queryResult.getItems()));
    }

    private CreateTableRequest buildTransactionsTableCreateRequest() {
        /* Table attributes definition */
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

        /* Table keys schema */
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement()
                .withAttributeName("id")
                .withKeyType(KeyType.HASH));

        /* Table GSI definition */
        GlobalSecondaryIndex userTransactionsIndex = new GlobalSecondaryIndex()
                .withIndexName("userTransactions-index")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        /* Table GSI keys */
        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();
        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("userId")
                .withKeyType(KeyType.HASH));

        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("created")
                .withKeyType(KeyType.RANGE));

        userTransactionsIndex.setKeySchema(indexKeySchema);

        /* Table create request definition */
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
}
