package com.clyde.sherlock.service;

import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.transaction.TransactionEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserTransactionsHandlingService {
    List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions);
    CompletableFuture<Void> handleTransactionEvents(TransactionEvent transactionEvent);
}
