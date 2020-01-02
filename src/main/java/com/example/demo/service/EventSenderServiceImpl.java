package com.example.demo.service;

import com.example.demo.model.transaction.TransactionEvent;
import com.example.demo.util.CompletablePromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;

@Service
public class EventSenderServiceImpl implements EventSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSenderServiceImpl.class);

    @Value("${kafka.topic.json}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Async
    public CompletableFuture<SendResult<String, TransactionEvent>> sendEvent(TransactionEvent transactionEvent) {
        ListenableFuture<SendResult<String, TransactionEvent>> sendTransactionEvent = kafkaTemplate.send(topic, transactionEvent);
        sendTransactionEvent.addCallback(new ListenableFutureCallback<SendResult<String, TransactionEvent>>() {

            @Override
            public void onSuccess(final SendResult<String, TransactionEvent> message) {
                LOGGER.info("sent message= " + message + " with offset= " + message.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(final Throwable throwable) {
                LOGGER.error("unable to send message= " + transactionEvent.toString(), throwable);
            }
        });

        return new CompletablePromise<>(sendTransactionEvent);
    }
}
