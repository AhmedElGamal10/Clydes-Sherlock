package com.example.demo.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@Service
public class PersistenceManagementServiceImpl implements PersistenceManagementService {

//    @Autowired
//    TransactionRepository transactionRepository;
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;
//    public List<Transaction> getUserPotentialTransactions(User user) {
//        return transactionRepository.queryUserTransactionsIndex(user, getPastDateByDifferenceInDays(5), getCurrentDate());
//    }

    @Override
    public void initialize() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest createTableRequest = dynamoDBMapper
                .generateCreateTableRequest(Transaction.class);

        createTableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(10L, 10L));
        createTableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
    }

//    public void createRecord(Transaction transaction) {
//        save(transaction);
//    }
//
//    public void updateRecord(Transaction transaction) {
//        save(transaction);
//    }

    @Override
//    public List<Transaction> queryUserTransactionsIndex(User user, String startDate, String endDate) {
    public List<Transaction> getUserPotentialTransactions(User user, String startDate, String endDate) {
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
