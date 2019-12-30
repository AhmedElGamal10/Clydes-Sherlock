package com.example.demo.repository;

import com.example.demo.model.transaction.Transaction;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface TransactionRepository extends CrudRepository<Transaction, String>, CustomTransactionRepository {

}