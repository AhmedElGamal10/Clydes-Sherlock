package com.example.demo.kafka;

import com.example.demo.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Value("${kafka.topic.json}")
    private String jsonTopic;

    @Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    public void send(Transaction transaction) {
        LOGGER.info("sending transaction='{}'", transaction.toString());
        kafkaTemplate.send(jsonTopic, transaction);
    }
}
