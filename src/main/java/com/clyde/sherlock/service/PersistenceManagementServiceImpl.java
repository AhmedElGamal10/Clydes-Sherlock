package com.clyde.sherlock.service;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.user.User;
import com.clyde.sherlock.repository.TransactionRepository;
import com.clyde.sherlock.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PersistenceManagementServiceImpl implements PersistenceManagementService {

    private static final Integer TIME_DIFFERENCE_IN_DAYS = 5;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public CompletableFuture<PutItemResult> save(final Transaction transaction) {
        return transactionRepository.saveTransaction(transaction);
    }

    @Override
    public CompletableFuture<List<Transaction>> getUserPotentialTransactions(final User user) {
        return transactionRepository.queryUserTransactionsIndex(
            user,
            DateUtils.getPastDateByDifferenceInDays(TIME_DIFFERENCE_IN_DAYS),
            DateUtils.getCurrentDate());
    }
}
