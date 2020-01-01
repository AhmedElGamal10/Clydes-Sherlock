package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RemoteServerLookupService {
//    CompletableFuture<List<User>> getSystemUsers();
    ListenableFuture<Response> getSystemUsers();
    CompletableFuture<List<Transaction>> getUserTransactions(User user);
}
