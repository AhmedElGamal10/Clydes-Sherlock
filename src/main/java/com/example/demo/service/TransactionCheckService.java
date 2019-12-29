package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;

import java.util.Map;

public interface TransactionCheckService {
    void handleUserTransaction(Transaction transaction, Map<String, Transaction> savedTransactionsMap);
}
