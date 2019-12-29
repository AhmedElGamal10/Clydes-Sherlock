package com.example.demo;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.service.RemoteServerLookupService;
import com.example.demo.service.TransactionCheckService;
import com.example.demo.service.PersistenceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(App.class);

    @Autowired
    private RemoteServerLookupService remoteServerLookupService;

    @Autowired
    private TransactionCheckService transactionCheckService;

    @Autowired
    private PersistenceService persistenceService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        persistenceService.initializeDatabase();

        while (true) {
            remoteServerLookupService.getSystemUsers().thenAcceptAsync(systemUsers -> {
                for (User user : systemUsers) {
                    System.out.println(user.getId());
                    List<Transaction> savedTransactions = persistenceService.getUserTransactions(user, getPastDateByDifferenceInDays(5), getCurrentDate());
                    Map<String, Transaction> savedTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));

                    List<Transaction> userTransactions = remoteServerLookupService.sendGetUserTransactionsRequest(user);
                        System.out.println(userTransactions.size());
                        for (Transaction remoteTransaction : userTransactions) {
                            System.out.println(remoteTransaction.getId());
                            remoteTransaction.setUserId(user.getId());
                            transactionCheckService.handleUserTransaction(remoteTransaction, savedTransactionsMap);
                        }
                }
            });
        }
    }
}
