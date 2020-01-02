//package com.example.demo.configuration;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.client.builder.AwsClientBuilder;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
//import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableDynamoDBRepositories(basePackages = "com.example.demo.repository")
//public class DynamoDBConfig {
//
//    @Value("${amazon.dynamodb.endpoint}")
//    private String dynamoDbEndpoint;
//
//    @Value("${amazon.aws.accessKey}")
//    private String awsAccessKey;
//
//    @Value("${amazon.aws.secretKey}")
//    private String awsSecretKey;
//
//    @Bean
//    public AmazonDynamoDB amazonDynamoDB() {
//        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000/", "us-west-1"))
//                .build();
//
//        return dynamoDB;
//    }
//
//    @Bean
//    public AWSCredentials amazonAWSCredentials() {
//        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
//    }
//}
