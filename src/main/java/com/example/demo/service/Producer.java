package com.example.demo.service;

import com.example.demo.model.transaction.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class Producer {

    public static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    @Value("${kafka.topic.json}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Async
    public void send(TransactionEvent transactionEvent) {
        ListenableFuture<SendResult<String, TransactionEvent>> future = kafkaTemplate.send(topic, transactionEvent);
        future.addCallback(new ListenableFutureCallback<SendResult<String, TransactionEvent>>() {

            @Override
            public void onSuccess(final SendResult<String, TransactionEvent> message) {
                LOGGER.info("sent message= " + message + " with offset= " + message.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(final Throwable throwable) {
                LOGGER.error("unable to send message= " + transactionEvent.toString(), throwable);
            }
        });
    }
}
