package com.example.demo;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;
import com.example.demo.model.user.User;
import com.example.demo.service.PersistenceManagementService;
import com.example.demo.service.RemoteServerLookupService;
import com.example.demo.service.UserTransactionsHandlingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class AsyncEventsSystemRunner implements CommandLineRunner {

    private final Logger LOGGER = LogManager.getLogger(AsyncEventsSystemRunner.class);

    @Autowired
    private RemoteServerLookupService remoteServerLookupService;

    @Autowired
    private UserTransactionsHandlingService userTransactionsHandlingService;

    @Autowired
    private PersistenceManagementService persistenceManagementService;

    @Override
    public void run(String... strings) throws Exception {
        while (true) {
            remoteServerLookupService.getSystemUsers().thenAcceptAsync(this::processUsersTransactions).get();
        }
    }

    private void processUsersTransactions(List<User> users) {
        users.stream().forEach(this::processUserTransactions);
    }

    private void processUserTransactions(User user) {
        try {
            fetchAllTransactionEventsForUser(user).get()
                    .stream().peek(x -> x.setUserId(user.getId()))
                    .forEach(userTransactionsHandlingService::handleTransactionEvents);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<List<TransactionEvent>> fetchAllTransactionEventsForUser(User user) {
        CompletableFuture<List<Transaction>> remoteTransactions = remoteServerLookupService.getUserTransactions(user);
        CompletableFuture<List<Transaction>> savedTransactions = persistenceManagementService.getUserPotentialTransactions(user);
        return remoteTransactions.thenCombine(savedTransactions, userTransactionsHandlingService::resolveConflicts).toCompletableFuture();
    }
}
