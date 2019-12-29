package com.example.demo;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.service.PersistenceManagementService;
import com.example.demo.service.RemoteServerLookupService;
import com.example.demo.service.UserTransactionsHandlingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AsyncEventsSystemRunner implements CommandLineRunner {

    private static final Logger LOGGER = LogManager.getLogger(AsyncEventsSystemRunner.class);

    @Autowired
    private RemoteServerLookupService remoteServerLookupService;

    @Autowired
    private UserTransactionsHandlingService userTransactionsHandlingService;

    @Autowired
    private PersistenceManagementService persistenceManagementService;

    @Override
    public void run(String... strings) throws Exception {
        Queue<User> systemUsers = new LinkedList<>(remoteServerLookupService.getSystemUsers().get());
        AtomicInteger systemUsersLastCount = new AtomicInteger(systemUsers.size());

        while (true) {
            remoteServerLookupService.getSystemUsers().thenAccept(users -> {
                systemUsers.addAll(users);
                systemUsersLastCount.set(users.size());
            });

            for(int i = 0; i < systemUsersLastCount.get() && !systemUsers.isEmpty(); i++) {
                User user = systemUsers.poll();

                if(user == null) continue;

                List<Transaction> savedTransactions = persistenceManagementService.getUserPotentialTransactions(user);
                userTransactionsHandlingService.setUserOldTransactions(savedTransactions);

                remoteServerLookupService.getUserTransactions(user).thenAcceptAsync(remoteUserTransactions -> {
                    for (Transaction remoteUserTransaction : remoteUserTransactions) {
                        remoteUserTransaction.setUserId(user.getId());
                        userTransactionsHandlingService.handleUserTransaction(remoteUserTransaction);
                    }
                });
            }
        }
    }
}