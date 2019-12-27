package com.example.demo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.AwsService;
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
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.client.RestTemplate;

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
//        for (User user : systemUsers) {
////            //Query DB for this user's transactions that has occurred during last 5 days
////
////            // take the proper action for each transaction:
////            // insert/event -- update/event - do nothing
////        }
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(AwsService.class);

        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);

        AwsService awsService = new AwsService();

        awsService.setServiceName("AWS DynamoDB");
        awsService.setServiceHomePageUrl("https://aws.amazon.com/dynamodb/?nc2=h_m1");

        awsService = awsServiceRepository.save(awsService);

        logger.info("Saved AwsService object: " + new Gson().toJson(awsService));

        String awsServiceId = awsService.getId();

        logger.info("AWS Service ID: " + awsServiceId);

        Optional<AwsService> awsServiceQueried = awsServiceRepository.findById(awsServiceId);

        if (awsServiceQueried.get() != null) {
            logger.info("Queried object: " + new Gson().toJson(awsServiceQueried.get()));
        }

        Iterable<AwsService> awsServices = awsServiceRepository.findAll();

        for (AwsService awsServiceObject : awsServices) {
            logger.info("List object: " + new Gson().toJson(awsServiceObject));
        }
    }

    private List<User> makeGetUsersRequest() {
        final String uri = "http://localhost:8081/clydescards.example.com/users";

        RestTemplate restTemplate = new RestTemplate();
        List<User> result = restTemplate.getForObject(uri, List.class);

        System.out.println(result);
        return result;
    }
}
