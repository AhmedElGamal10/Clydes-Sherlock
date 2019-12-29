package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.example.demo.util.DateUtils.getCurrentDate;

@Service
public class UserTransactionHandlerServiceImpl implements UserTransactionHandlerService {
    private final static double EPS = 1e-7;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventSenderServiceImpl eventSenderServiceImpl;

    @Override
    public void handleUserTransaction(Transaction transaction, Map<String, Transaction> savedTransactionsMap) {
        if (!savedTransactionsMap.containsKey(transaction.getId())) {
            handleNewTransaction(transaction);
        } else if (transactionHasChanged(transaction, savedTransactionsMap.get(transaction.getId()))) {
            handleUpdatedTransaction(transaction);
        }
    }

    @Transactional
    private void handleNewTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        eventSenderServiceImpl.sendEvent(buildCreatedTransactionEvent(transaction));
    }

    @Transactional
    private void handleUpdatedTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        eventSenderServiceImpl.sendEvent(buildUpdatedTransactionEvent(transaction));
    }

    private TransactionEvent buildUpdatedTransactionEvent(Transaction transaction) {
        return new TransactionEvent(transaction, TransactionEvent.TRANSACTION_EVENT_TYPE.UPDATE, getCurrentDate());
    }

    private TransactionEvent buildCreatedTransactionEvent(Transaction transaction) {
        return new TransactionEvent(transaction, TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE, getCurrentDate());
    }

    private boolean transactionHasChanged(Transaction remoteTransaction, Transaction localTransaction) {
        return remoteTransaction.getCreated().equals(localTransaction.getCreated()) &&
                remoteTransaction.getState().equals(localTransaction.getState()) &&
                remoteTransaction.getUserId().equals(localTransaction.getUserId()) &&
                remoteTransaction.getAmount() - localTransaction.getAmount() < EPS;
    }
}
