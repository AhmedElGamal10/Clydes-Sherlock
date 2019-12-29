package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.Map;

public interface PersistenceManagementService {
    void initializeDatabase();
    List<Transaction> getUserTransactions(User user, String fiveDaysAgoDate, String currentDate);
}
