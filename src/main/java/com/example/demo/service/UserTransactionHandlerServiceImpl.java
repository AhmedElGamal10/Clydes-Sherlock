package com.example.demo.service;

import com.example.demo.kafka.Sender;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionEvent;
import com.example.demo.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class UserTransactionHandlerServiceImpl implements UserTransactionHandlerService {
    private final static double EPS = 1e-7;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private Sender sender;

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
        // write into DDB
        transactionRepository.save(transaction);
        sender.send(new TransactionEvent(transaction, TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE, getCurrentDate()));
    }

    @Transactional
    private void handleUpdatedTransaction(Transaction transaction) {
        // update DDB
        transactionRepository.save(transaction);
        sender.send(new TransactionEvent(transaction, TransactionEvent.TRANSACTION_EVENT_TYPE.UPDATE, getCurrentDate()));
    }

    private boolean transactionHasChanged(Transaction remoteTransaction, Transaction localTransaction) {
        return remoteTransaction.getCreated().equals(localTransaction.getCreated()) &&
                remoteTransaction.getState().equals(localTransaction.getState()) &&
                remoteTransaction.getUserId().equals(localTransaction.getUserId()) &&
                remoteTransaction.getAmount() - localTransaction.getAmount() < EPS;
    }

    private String getCurrentDate() {
        return getPastDateByDifferenceInDays(0);
    }

    private String getPastDateByDifferenceInDays(int differenceInDays) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        return dtf.format(today.plusDays(-1 * differenceInDays));
    }
}
