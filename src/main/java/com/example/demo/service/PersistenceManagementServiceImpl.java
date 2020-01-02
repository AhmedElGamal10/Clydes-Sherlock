package com.example.demo.service;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@Service
public class PersistenceManagementServiceImpl implements PersistenceManagementService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public CompletableFuture<PutItemResult> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public CompletableFuture<List<Transaction>> getUserPotentialTransactions(User user) {
        return transactionRepository.queryUserTransactionsIndex(user, getPastDateByDifferenceInDays(5), getCurrentDate());
    }
}
