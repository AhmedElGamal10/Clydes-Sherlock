package com.clyde.sherlock.service;

import com.clyde.sherlock.producer.TransactionEventProducer;
import com.clyde.sherlock.util.DateUtils;
import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.transaction.TransactionEvent;
import com.clyde.sherlock.model.transaction.TransactionEvent.TransactionEventType;
import com.clyde.sherlock.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserTransactionsHandlingServiceImpl implements UserTransactionsHandlingService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private TransactionEventProducer transactionEventProducer;

  @Transactional
  public CompletableFuture<Void> handleTransactionEvents(
      final TransactionEvent transactionEvent
  ) {
    return CompletableFuture.allOf(
        transactionRepository.saveTransaction(transactionEvent.getTransaction()),
        transactionEventProducer.sendEvent(transactionEvent));
  }

  public List<TransactionEvent> resolveConflicts(
      final List<Transaction> remoteTransactions,
      final List<Transaction> savedTransactions
  ) {
    final Map<String, Transaction> savedTransactionsMap = savedTransactions
        .stream()
        .collect(Collectors.toMap(Transaction::getId, Function.identity()));

    return remoteTransactions
        .stream()
        .filter(transaction -> !transaction.equals(
            savedTransactionsMap.getOrDefault(transaction.getId(), null)))
        .map(transaction -> new TransactionEvent(
            transaction,
            savedTransactionsMap.containsKey(transaction.getId()) ? TransactionEventType.UPDATE
                : TransactionEventType.CREATE, DateUtils.getCurrentDate()))
        .collect(Collectors.toList());
  }
}
