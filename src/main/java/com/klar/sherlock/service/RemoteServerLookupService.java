package com.klar.sherlock.service;

import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RemoteServerLookupService {
    CompletableFuture<List<User>> getSystemUsers();
    CompletableFuture<List<Transaction>> getUserTransactions(User user);
}
