package com.klar.sherlock.service;

import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.transaction.TransactionEvent;
import com.klar.sherlock.model.transaction.TransactionEvent.TransactionEventType;
import com.klar.sherlock.repository.TransactionRepository;
import com.klar.sherlock.util.DateUtils;
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
    private EventSenderService eventSenderService;

    @Transactional
    public CompletableFuture<Void> handleTransactionEvents(TransactionEvent transactionEvent) {
        return CompletableFuture.allOf(transactionRepository.saveTransaction(transactionEvent.getTransaction()),
                eventSenderService.sendEvent(transactionEvent));
    }

    public List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions) {
        Map<String, Transaction> savedTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));

        return remoteTransactions.stream().filter(x -> !x.equals(savedTransactionsMap.getOrDefault(x.getId(), null)))
                .map(x -> new TransactionEvent(x, savedTransactionsMap.containsKey(x.getId()) ? TransactionEventType.UPDATE : TransactionEventType.CREATE, DateUtils.getCurrentDate()))
                .collect(Collectors.toList());
    }
}
