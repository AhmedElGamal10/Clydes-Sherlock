//package com.example.demo.service;
//
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
//import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
//import com.amazonaws.services.dynamodbv2.util.TableUtils;
//import com.example.demo.repositories.ExtendedTransaction;
//import com.example.demo.repositories.TransactionRepository;
//import com.google.gson.Gson;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Slf4j
//@Service
//public class TransactionService {
//
//    private DynamoDBMapper dynamoDBMapper;
//
//    @Autowired
//    private AmazonDynamoDB amazonDynamoDB;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//
//    public void operate() {
//        System.out.println(amazonDynamoDB);
//        System.out.println(transactionRepository);
//        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
//
//        CreateTableRequest tableRequest = dynamoDBMapper
//                .generateCreateTableRequest(ExtendedTransaction.class);
//
//        tableRequest.setProvisionedThroughput(
//                new ProvisionedThroughput(1L, 1L));
//
//        TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);
//
//        ExtendedTransaction transaction = new ExtendedTransaction();
//
//        transaction.setAmount(50);
//        transaction.setTransactionId("transaction_1");
//        transaction.setDate("1st Jan 2019");
//        transaction.setState("AUTHORIZED");
//        transaction.setUserId("user_1");
//
//        transaction = transactionRepository.save(transaction);
//
//        log.info("Saved AwsService object: " + new Gson().toJson(transaction));
//        /************************************************************************************/
//        String transactionId = transaction.getTransactionId();
//
//        log.info("AWS Service ID: " + transactionId);
//
//        Optional<ExtendedTransaction> awsServiceQueried = transactionRepository.findById(transaction.getUserId());
//
//        if (awsServiceQueried.get() != null) {
//            log.info("Queried object: " + new Gson().toJson(awsServiceQueried.get()));
//        }
//
//        Iterable<ExtendedTransaction> awsServices = transactionRepository.findAll();
//
//        for (ExtendedTransaction awsServiceObject : awsServices) {
//            log.info("List object: " + new Gson().toJson(awsServiceObject));
//        }
//    }
//}
