package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;

import java.util.List;
import java.util.Map;

public interface UserTransactionsHandlingService {
    void handleUserTransaction(Transaction transaction);

    void setUserOldTransactions(List<Transaction> savedTransactions);
}
