package com.klar.sherlock.service;

import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.transaction.TransactionEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserTransactionsHandlingService {
    List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions);
    CompletableFuture<Void> handleTransactionEvents(TransactionEvent transactionEvent);
}
