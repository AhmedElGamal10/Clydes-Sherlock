package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.Map;

public interface PersistenceManagementService {
    void initialize();
    List<Transaction> getUserPotentialTransactions(User user, String startDate, String endDate);
//    List<Transaction> queryUserTransactionsIndex(User user, String startDate, String endDate);
//    List<Transaction> getUserPotentialTransactions(User user);
}
