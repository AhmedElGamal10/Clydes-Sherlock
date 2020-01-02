package com.example.demo.service;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PersistenceManagementService {
    CompletableFuture<PutItemResult> save(Transaction transaction);
    CompletableFuture<List<Transaction>> getUserPotentialTransactions(User user);
}
