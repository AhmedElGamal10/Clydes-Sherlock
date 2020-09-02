package com.clyde.sherlock.service;

import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RemoteServerLookupService {
    CompletableFuture<List<User>> getSystemUsers();
    CompletableFuture<List<Transaction>> getUserTransactions(User user);
}
