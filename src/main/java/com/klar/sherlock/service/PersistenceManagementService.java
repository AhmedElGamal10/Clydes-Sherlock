package com.klar.sherlock.service;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PersistenceManagementService {
    CompletableFuture<PutItemResult> save(Transaction transaction);
    CompletableFuture<List<Transaction>> getUserPotentialTransactions(User user);
}
