package com.example.demo.repositories;

import com.example.demo.model.AwsService;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface AwsServiceRepository extends CrudRepository<AwsService, String> {
}