package com.example.demo;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
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
        persistenceManagementService.initializeDatabase();

        while (true) {
            remoteServerLookupService.sendGetSystemUsersRequest().thenAcceptAsync(systemUsers -> {
                for (User user : systemUsers) {
                    List<Transaction> savedTransactions = persistenceManagementService.getUserTransactions(user, getPastDateByDifferenceInDays(5), getCurrentDate());
                    userTransactionsHandlingService.setUserOldTransactions(savedTransactions);

                    List<Transaction> remoteUserTransactions = remoteServerLookupService.sendGetUserTransactionsRequest(user);
                    for (Transaction remoteUserTransaction : remoteUserTransactions) {
                        remoteUserTransaction.setUserId(user.getId());
                        userTransactionsHandlingService.handleUserTransaction(remoteUserTransaction);
                    }
                }
            });
        }
    }
}
