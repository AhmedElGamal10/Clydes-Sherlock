package com.example.demo;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.RemoteServerLookupService;
import com.example.demo.service.UserTransactionsHandlingService;
import com.example.demo.service.PersistenceManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(App.class);

    @Autowired
    private RemoteServerLookupService remoteServerLookupService;

    @Autowired
    private UserTransactionsHandlingService userTransactionsHandlingService;

    @Autowired
    private PersistenceManagementService persistenceManagementService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        Queue<User> systemUsers = new LinkedList<>(remoteServerLookupService.sendGetSystemUsersRequest().get());
        AtomicInteger systemUsersLastCount = new AtomicInteger(systemUsers.size());

        while (true) {
            remoteServerLookupService.sendGetSystemUsersRequest().thenAccept(users -> {
                systemUsers.addAll(users);
                systemUsersLastCount.set(users.size());
            });

            for(int i = 0; i < systemUsersLastCount.get() && !systemUsers.isEmpty(); i++) {
                User user = systemUsers.poll();
                List<Transaction> savedTransactions = persistenceManagementService.getUserPotentialTransactions(user);
                userTransactionsHandlingService.setUserOldTransactions(savedTransactions);

                remoteServerLookupService.sendGetUserTransactionsRequest(user).thenAcceptAsync(remoteUserTransactions -> {
                    for (Transaction remoteUserTransaction : remoteUserTransactions) {
                        remoteUserTransaction.setUserId(user.getId());
                        userTransactionsHandlingService.handleUserTransaction(remoteUserTransaction);
                    }
                });
            }
        }
    }
}
