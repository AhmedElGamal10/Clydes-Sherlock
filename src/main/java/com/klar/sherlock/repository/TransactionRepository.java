package com.klar.sherlock.repository;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionRepository {
    CompletableFuture<PutItemResult> saveTransaction(Transaction transaction);
    CompletableFuture<List<Transaction>> queryUserTransactionsIndex(User user, String startDate, String endDate);
}
