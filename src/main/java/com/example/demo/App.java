package com.example.demo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.AwsService;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionId;
import com.example.demo.model.User;
import com.example.demo.repositories.AwsServiceRepository;
import com.example.demo.repositories.TransactionRepository;
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
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(App.class);
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private AwsServiceRepository awsServiceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        List<User> systemUsers = makeGetUsersRequest();

        for (User user : systemUsers) {
            List<Transaction> userTransactions = sendTransactionsRequest(user);

//            List<Transaction> savedUserTransactions = queryUserTransactions(user);

//            //Query DB for this user's transactions that has occurred during last 5 days
//
//            // take the proper action for each transaction:
//            // insert/event -- update/event - do nothing
        }
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(Transaction.class);

        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);

        Transaction transaction = new Transaction();
        transaction.setAmount(50.8);
        transaction.setCreated("2019-12-30");
        transaction.setId("transaction_3");
        transaction.setState("AUTHORIZED");

        transaction = transactionRepository.save(transaction);

        logger.info("Saved Transaction object: " + new Gson().toJson(transaction));

        TransactionId transactionId = new TransactionId();
        transactionId.setCreated(transaction.getCreated());
        transactionId.setId(transaction.getId());

        Optional<Transaction> transactionQueried = transactionRepository.findById(transactionId);

        if (transactionQueried.get() != null) {
            logger.info("Queried object: " + new Gson().toJson(transactionQueried.get()));
        }

        Iterable<Transaction> transactions = transactionRepository.findAll();

        for (Transaction transactionObject : transactions) {
            logger.info("List object: " + new Gson().toJson(transactionObject));
        }
    }

    private List<Transaction> sendTransactionsRequest(User user) {
        String uri = buildTransactionsUri(user);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Transaction>> userTransactionsResponse =
                restTemplate.exchange(uri,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Transaction>>() {
                        });
        List<Transaction> userTransactions = userTransactionsResponse.getBody();
        return userTransactions;
    }

    private List<User> makeGetUsersRequest() {
        final String uri = "http://localhost:8081/clydescards.example.com/users";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<User>> usersResponse =
                restTemplate.exchange(uri,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                        });
        List<User> users = usersResponse.getBody();

        return users;
    }

    private String buildTransactionsUri(User user) {
        final String baseUri = "http://localhost:8081/clydescards.example.com/transactions?userId=";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        String currentDate = dtf.format(today);
        String fiveDaysAgoDate = dtf.format(today.plusDays(-5));

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
}
