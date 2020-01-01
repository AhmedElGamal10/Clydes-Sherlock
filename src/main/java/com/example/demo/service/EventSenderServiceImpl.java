package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import static com.example.demo.util.DateUtils.getCurrentDate;

public class EventSenderServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSenderServiceImpl.class);

    @Value("${kafka.topic.json}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void sendEvent(TransactionEvent transactionEvent) {
        LOGGER.info("sending transaction event='{}'", transactionEvent.toString());
        kafkaTemplate.send(topic, transactionEvent);
    }
}
