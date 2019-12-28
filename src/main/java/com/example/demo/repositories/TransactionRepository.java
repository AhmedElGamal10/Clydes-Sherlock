package com.example.demo.repositories;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionId;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface TransactionRepository extends CrudRepository<Transaction, TransactionId> {
}