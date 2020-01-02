package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionRepository {
    CompletableFuture<PutItemResult> saveTransaction(Transaction transaction);
    CompletableFuture<List<Transaction>> queryUserTransactionsIndex(User user, String startDate, String endDate);
}
