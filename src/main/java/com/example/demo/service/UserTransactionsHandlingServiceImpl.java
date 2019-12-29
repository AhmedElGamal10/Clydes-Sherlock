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
    private final static double EPS = 1e-7;
    private Map<String, Transaction> userOldTransactionsMap;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventSenderServiceImpl eventSenderServiceImpl;

    @Override
    public void handleUserTransaction(Transaction remoteUserTransaction) {
        if (!userOldTransactionsMap.containsKey(remoteUserTransaction.getId())) {
            handleNewOrUpdatedTransaction(remoteUserTransaction, TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE);
        } else if (transactionHasChanged(remoteUserTransaction, userOldTransactionsMap.get(remoteUserTransaction.getId()))) {
            handleNewOrUpdatedTransaction(remoteUserTransaction, TransactionEvent.TRANSACTION_EVENT_TYPE.UPDATE);
        }
    }

    @Transactional
    private void handleNewOrUpdatedTransaction(Transaction transaction, TransactionEvent.TRANSACTION_EVENT_TYPE eventType) {
        transactionRepository.save(transaction);
        eventSenderServiceImpl.sendEvent(new TransactionEvent(transaction, eventType, getCurrentDate()));
    }

    private boolean transactionHasChanged(Transaction remoteTransaction, Transaction localTransaction) {
        return remoteTransaction.getCreated().equals(localTransaction.getCreated()) &&
                remoteTransaction.getState().equals(localTransaction.getState()) &&
                remoteTransaction.getUserId().equals(localTransaction.getUserId()) &&
                remoteTransaction.getAmount() - localTransaction.getAmount() < EPS;
    }

    @Override
    public void setUserOldTransactions(List<Transaction> savedTransactions) {
        userOldTransactionsMap = savedTransactions.stream().collect(Collectors.toMap(Transaction::getId, Function.identity()));
    }
}
