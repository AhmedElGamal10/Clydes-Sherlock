package com.example.demo.repositories;

import com.example.demo.model.Transaction;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface TransactionRepository extends CrudRepository<Transaction, String> {
}