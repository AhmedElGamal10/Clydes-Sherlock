package com.example.demo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.service.ClydesCardsLookupService;
import com.example.demo.service.UserTransactionHandlerService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(App.class);
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private ClydesCardsLookupService clydesCardsLookupService;

    @Autowired
    private UserTransactionHandlerService userTransactionHandlerService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        initializeDynamoDB();

        /* Set rate to 2 requests per second .. to be increased to 200 (as required) later */
        RateLimiter rateLimiter = RateLimiter.create(2);

        while (true) {
            CompletableFuture<List<User>> systemUsers = clydesCardsLookupService.getSystemUsers(rateLimiter);
            for (User user : systemUsers.get()) {
                CompletableFuture<List<Transaction>> userTransactions = clydesCardsLookupService.sendGetUserTransactionsRequest(user, rateLimiter);
                List<Transaction> savedTransactions = getUserTransactions(user, getPastDateByDifferenceInDays(5), getCurrentDate());

                Map<String, Transaction> savedTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));
                for (Transaction remoteTransaction : userTransactions.get()) {
                    remoteTransaction.setUserId(user.getId());
                    userTransactionHandlerService.handleUserTransaction(remoteTransaction, savedTransactionsMap);
                }
            }
        }
    }

    private void initializeDynamoDB() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest createTableRequest = dynamoDBMapper
                .generateCreateTableRequest(Transaction.class);

        createTableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(10L, 10L));
        createTableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
    }

    private List<Transaction> getUserTransactions(User user, String fiveDaysAgoDate, String currentDate) {
        Map<String, String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#userId", "userId");
        expressionAttributesNames.put("#created", "created");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userId", new AttributeValue().withS(user.getId()));
        expressionAttributeValues.put(":fiveDaysAgoDate", new AttributeValue().withS(fiveDaysAgoDate));
        expressionAttributeValues.put(":currentDate", new AttributeValue().withS(currentDate));

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
