package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.util.DateUtils.getCurrentDate;

@Service
public class UserTransactionsHandlingServiceImpl implements UserTransactionsHandlingService {

    private Map<String, Transaction> userOldTransactionsMap;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventSenderServiceImpl eventSenderServiceImpl;

    @Override
    public void handleUserTransaction(Transaction remoteUserTransaction) {
//        if (!userOldTransactionsMap.containsKey(remoteUserTransaction.getId())) {
//            handleTransactionEvents(remoteUserTransaction, TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE);
//        } else if (!equalTransactions(remoteUserTransaction, userOldTransactionsMap.get(remoteUserTransaction.getId()))) {
//            handleNewOrUpdatedTransaction(remoteUserTransaction, TransactionEvent.TRANSACTION_EVENT_TYPE.UPDATE);
//        }
    }

//    @Transactional
    public void handleTransactionEvents(TransactionEvent transactionEvent) {
        transactionRepository.save(transactionEvent);
        eventSenderServiceImpl.sendEvent(transactionEvent);
    }

    @Override
    public void setUserOldTransactions(List<Transaction> savedTransactions) {
        userOldTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));
    }

    public List<TransactionEvent> resolveConflicts(List<Transaction> remoteTransactions, List<Transaction> savedTransactions) {
        Map<String, Transaction> savedTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));

        return remoteTransactions.stream().filter(x -> !x.equals(savedTransactionsMap.getOrDefault(x.getId(), null)))
                .map(x -> new TransactionEvent(x.getTransaction(), savedTransactionsMap.containsKey(x.getId())? TransactionEvent.TRANSACTION_EVENT_TYPE.UPDATE : TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE, getCurrentDate()))
                .collect(Collectors.toList());
    }
}
