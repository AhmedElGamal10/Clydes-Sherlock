package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserTransactionsHandlingService {
    List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions);
    CompletableFuture<Void> handleTransactionEvents(TransactionEvent transactionEvent);
}
