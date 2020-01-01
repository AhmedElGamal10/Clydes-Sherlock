package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;

import java.util.List;

public interface UserTransactionsHandlingService {
    List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions);
    void handleTransactionEvents(TransactionEvent transactionEvent);
}
