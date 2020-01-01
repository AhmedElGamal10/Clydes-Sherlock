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
        Transaction t = new Transaction();
        t.setUserId("aaa");
        t.setId("hjbfvdf");
        t.setState("aaa");
        t.setCreated("2019-12-31");
        t.setAmount(50.88);
        TransactionEvent event = new TransactionEvent(t, TransactionEvent.TRANSACTION_EVENT_TYPE.CREATE, getCurrentDate());
        kafkaTemplate.send(topic, event);
    }
}
