package com.klar.sherlock;

import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.transaction.TransactionEvent;
import com.klar.sherlock.model.user.User;
import com.klar.sherlock.service.PersistenceManagementService;
import com.klar.sherlock.service.RemoteServerLookupService;
import com.klar.sherlock.service.UserTransactionsHandlingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class SherlockApplicationRunner implements CommandLineRunner {

    private final Logger LOGGER = LogManager.getLogger(SherlockApplicationRunner.class);

    @Autowired
    private RemoteServerLookupService remoteServerLookupService;

    @Autowired
    private UserTransactionsHandlingService userTransactionsHandlingService;

    @Autowired
    private PersistenceManagementService persistenceManagementService;

    @Override
    public void run(String... strings) {
        while (true) {
            remoteServerLookupService.getSystemUsers().thenCompose(this::processUsersTransactions).join();
        }
    }

    private CompletableFuture<Void> processUsersTransactions(List<User> users) {
        List<CompletableFuture<Void>> futures = users.stream().map(this::processUserTransactions).collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
    }

    private CompletableFuture<Void> processUserTransactions(User user) {
        return fetchAllTransactionEventsForUser(user).thenCompose(events -> {
                    List<CompletableFuture<Void>> futures = events.stream().peek(x -> x.setUserId(user.getId()))
                            .map(userTransactionsHandlingService::handleTransactionEvents).collect(Collectors.toList());
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
                }
        );
    }

    private CompletableFuture<List<TransactionEvent>> fetchAllTransactionEventsForUser(User user) {
        CompletableFuture<List<Transaction>> remoteTransactions = remoteServerLookupService.getUserTransactions(user);
        CompletableFuture<List<Transaction>> savedTransactions = persistenceManagementService.getUserPotentialTransactions(user);
        return remoteTransactions.thenCombine(savedTransactions, userTransactionsHandlingService::resolveConflicts).toCompletableFuture();
    }
}
