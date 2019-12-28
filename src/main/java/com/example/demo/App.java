package com.example.demo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repositories.TransactionRepository;
import com.example.demo.service.KafkaSender;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(App.class);
    private DynamoDBMapper dynamoDBMapper;
    private final static double EPS = 1e-7;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaSender kafkaSender;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        initializeDynamoDB();

        /* Set rate to 2 requests per second .. to be increased to 200 (as required) later */
        RateLimiter rateLimiter = RateLimiter.create(2);

        insertAndLogForDevelopment();

        while(true) {
            kafkaSender.send("msg");
            List<User> systemUsers = sendGetUsersRequest(rateLimiter);
            for (User user : systemUsers) {
                List<Transaction> userTransactions = sendGetUserTransactionsRequest(user, rateLimiter);
                List<Transaction> savedTransactions = getUserTransactions(user, getPastDateByDifferenceInDays(5), getCurrentDate());

                Map<String, Transaction> savedTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));
                for (Transaction remoteTransaction : userTransactions) {
                    if (!savedTransactionsMap.containsKey(remoteTransaction.getId())) {
                        // write into DDB and produce event
                    } else if (transactionHasChanged(remoteTransaction, savedTransactionsMap.get(remoteTransaction.getId()))) {
                        // update DDB and produce event
                    }
                }
            }
        }
    }

    private boolean transactionHasChanged(Transaction remoteTransaction, Transaction localTransaction) {
        return remoteTransaction.getCreated().equals(localTransaction.getCreated()) &&
                remoteTransaction.getState().equals(localTransaction.getState()) &&
                remoteTransaction.getUserId().equals(localTransaction.getUserId()) &&
                remoteTransaction.getAmount() - localTransaction.getAmount() < EPS;
    }

    /* Temporarily for development */
    private void insertAndLogForDevelopment() {
        Transaction transaction = new Transaction();
        transaction.setAmount(50.8);
        transaction.setCreated("2019-12-27");
        transaction.setId("transaction_5");
        transaction.setState("AUTHORIZED");
        transaction.setUserId("user_2");
        transaction = transactionRepository.save(transaction);

        logger.info("Saved Transaction object: " + new Gson().toJson(transaction));

        Optional<Transaction> transactionQueried = transactionRepository.findById(transaction.getId());
        if (transactionQueried.get() != null) {
            logger.info("Queried object: " + new Gson().toJson(transactionQueried.get()));
        }

        Iterable<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transactionObject : transactions) {
            logger.info("List object: " + new Gson().toJson(transactionObject));
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

    private List<Transaction> sendGetUserTransactionsRequest(User user, RateLimiter rateLimiter) {
//        rateLimiter.acquire();
        System.out.println("made 1 request during: " + rateLimiter.acquire() + "s");

        String uri = buildUserTransactionsRequestPath(user);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Transaction>> userTransactionsResponse =
                restTemplate.exchange(uri,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Transaction>>() {
                        });
        List<Transaction> userTransactions = userTransactionsResponse.getBody();
        return userTransactions;
    }

    private List<User> sendGetUsersRequest(RateLimiter rateLimiter) {
//        rateLimiter.acquire();
        System.out.println("made 1 request during: " + rateLimiter.acquire() + "s");

        final String uri = "http://localhost:8081/clydescards.example.com/users";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<User>> usersResponse =
                restTemplate.exchange(uri,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                        });
        List<User> users = usersResponse.getBody();

        return users;
    }

    private String buildUserTransactionsRequestPath(User user) {
        final String baseUri = "http://localhost:8081/clydescards.example.com/transactions?userId=";

        String currentDate = getCurrentDate();
        String fiveDaysAgoDate = getPastDateByDifferenceInDays(5);

        StringBuilder sb = new StringBuilder();
        sb.append(baseUri);
        sb.append(user.getId());

        sb.append("&");
        sb.append("startDate=");
        sb.append(currentDate);

        sb.append("&");
        sb.append("endDate=");
        sb.append(fiveDaysAgoDate);

        return sb.toString();
    }

    private String getCurrentDate() {
        return getPastDateByDifferenceInDays(0);
    }

    private String getPastDateByDifferenceInDays(int differenceInDays) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        return dtf.format(today.plusDays(-1 * differenceInDays));
    }
}
