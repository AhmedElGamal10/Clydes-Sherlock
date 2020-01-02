package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.util.DateUtils.getCurrentDate;

@Service
public class UserTransactionsHandlingServiceImpl implements UserTransactionsHandlingService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventSenderService eventSenderService;

    @Transactional
    public void handleTransactionEvents(TransactionEvent transactionEvent) {
        try {
            transactionRepository.save(transactionEvent.getTransaction()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        eventSenderService.sendEvent(transactionEvent);
    }

    public List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions) {
        Map<String, Transaction> savedTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));

        return remoteTransactions.stream().filter(x -> !x.equals(savedTransactionsMap.getOrDefault(x.getId(), null)))
                .map(x -> new TransactionEvent(x, savedTransactionsMap.containsKey(x.getId())? TransactionEvent.TRANSACTION_EVENT_TYPE.UPDATE : TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE, getCurrentDate()))
                .collect(Collectors.toList());
    }
}
