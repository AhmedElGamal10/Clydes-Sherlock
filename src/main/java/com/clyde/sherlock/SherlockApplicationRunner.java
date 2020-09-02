package com.clyde.sherlock;

import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.transaction.TransactionEvent;
import com.clyde.sherlock.model.user.User;
import com.clyde.sherlock.service.PersistenceManagementService;
import com.clyde.sherlock.service.RemoteServerLookupService;
import com.clyde.sherlock.service.UserTransactionsHandlingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SherlockApplicationRunner implements CommandLineRunner {

    @Autowired
    private RemoteServerLookupService remoteServerLookupService;

    @Autowired
    private UserTransactionsHandlingService userTransactionsHandlingService;

    @Autowired
    private PersistenceManagementService persistenceManagementService;

    @Override
    public void run(String... strings) {
        while (true) {
            remoteServerLookupService
                .getSystemUsers()
                .thenCompose(this::processUsersTransactions).join();
        }
    }

    private CompletableFuture<Void> processUsersTransactions(final List<User> users) {
        final List<CompletableFuture<Void>> futures = users
            .stream()
            .map(this::processUserTransactions)
            .collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
    }

    private CompletableFuture<Void> processUserTransactions(final User user) {
        return fetchAllTransactionEventsForUser(user).thenCompose(events -> {
                final List<CompletableFuture<Void>> futures = events
                    .stream()
                    .peek(transactionEvent -> transactionEvent.setUserId(user.getId()))
                    .map(userTransactionsHandlingService::handleTransactionEvents)
                    .collect(Collectors.toList());
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            }
        );
    }

    private CompletableFuture<List<TransactionEvent>> fetchAllTransactionEventsForUser(final User user) {
        final CompletableFuture<List<Transaction>> remoteTransactions =
            remoteServerLookupService.getUserTransactions(user);
        final CompletableFuture<List<Transaction>> savedTransactions =
            persistenceManagementService.getUserPotentialTransactions(user);
        return remoteTransactions.thenCombine(
            savedTransactions,
            userTransactionsHandlingService::resolveConflicts)
            .toCompletableFuture();
    }
}
