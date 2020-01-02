package com.klar.sherlock.service;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.user.User;
import com.klar.sherlock.repository.TransactionRepository;
import com.klar.sherlock.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PersistenceManagementServiceImpl implements PersistenceManagementService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public CompletableFuture<PutItemResult> save(Transaction transaction) {
        return transactionRepository.saveTransaction(transaction);
    }

    @Override
    public CompletableFuture<List<Transaction>> getUserPotentialTransactions(User user) {
        return transactionRepository.queryUserTransactionsIndex(user, DateUtils.getPastDateByDifferenceInDays(5), DateUtils.getCurrentDate());
    }
}
