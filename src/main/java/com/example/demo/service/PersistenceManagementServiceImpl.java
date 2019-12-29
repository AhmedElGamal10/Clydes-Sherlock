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

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getUserPotentialTransactions(User user) {
        return transactionRepository.queryUserTransactionsIndex(user, getPastDateByDifferenceInDays(5), getCurrentDate());
    }
}
