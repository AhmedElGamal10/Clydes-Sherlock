//package com.example.demo.repository;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.client.builder.AwsClientBuilder;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import com.amazonaws.services.dynamodbv2.document.DynamoDB;
//
///**
// * DynamoDB local link : http://docs.aws.amazon.com/ko_kr/amazondynamodb/latest/developerguide/DynamoDBLocal.html
// */
//@Configuration
//public class DynamoDbClientConfigurationBean {
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
//    @Bean(destroyMethod = "shutdown")
//    public AmazonDynamoDBAsync amazonDynamoDBAsync() {
//        return AmazonDynamoDBAsyncClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000/", "us-west-1"))
//                .build();
//    }
//
//    @Bean(destroyMethod = "shutdown")
//    public DynamoDB dynamoDB(final AmazonDynamoDBAsync amazonDynamoDBAsync) {
//        return new DynamoDB(amazonDynamoDBAsync);
//    }
//
//    @Bean
//    public DynamoDBMapper dynamoDBMapper(final AmazonDynamoDBAsync amazonDynamoDBAsync) {
//        return new DynamoDBMapper(amazonDynamoDBAsync);
//    }
//}
