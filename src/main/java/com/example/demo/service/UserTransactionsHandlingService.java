package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;

import java.util.List;

public interface UserTransactionsHandlingService {
    void handleUserTransaction(Transaction transaction);
    void setUserOldTransactions(List<Transaction> savedTransactions);
}
