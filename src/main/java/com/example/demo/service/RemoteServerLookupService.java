package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RemoteServerLookupService {
    CompletableFuture<List<User>> getSystemUsers();
    List<Transaction> sendGetUserTransactionsRequest(User user);
}
