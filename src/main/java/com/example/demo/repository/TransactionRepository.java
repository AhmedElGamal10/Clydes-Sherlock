package com.example.demo.repository;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface TransactionRepository extends CrudRepository<Transaction, String> {
    void createRecord(Transaction transaction);
    void updateRecord(Transaction transaction);
    List<Transaction> queryUserTransactionsIndex(User user, String startDate, String endDate);
}