package com.clyde.sherlock.repository;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionRepository {
    CompletableFuture<PutItemResult> saveTransaction(Transaction transaction);
    CompletableFuture<List<Transaction>> queryUserTransactionsIndex(User user, String startDate, String endDate);
}
