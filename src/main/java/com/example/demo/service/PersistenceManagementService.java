package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import java.util.List;
import java.util.Map;

public interface PersistenceManagementService {
    List<Transaction> getUserPotentialTransactions(User user);
}
