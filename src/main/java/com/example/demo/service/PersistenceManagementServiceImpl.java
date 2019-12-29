package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@Service
public class PersistenceManagementServiceImpl implements PersistenceManagementService {

    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> getUserPotentialTransactions(User user) {
        return transactionRepository.queryUserTransactionsIndex(user, getPastDateByDifferenceInDays(5), getCurrentDate());
    }
}
