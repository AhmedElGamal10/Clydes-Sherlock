package com.example.demo.service;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

public interface PersistenceManagementService {
    PutItemResult save(Transaction transaction);
    List<Transaction> getUserPotentialTransactions(User user);
}
