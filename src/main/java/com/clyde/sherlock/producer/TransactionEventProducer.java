package com.clyde.sherlock.producer;

import com.clyde.sherlock.model.transaction.TransactionEvent;
import com.clyde.sherlock.util.CompletablePromise;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TransactionEventProducer {

    @Value("${transactionevent.topic.name}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Async
    public CompletableFuture<SendResult<String, TransactionEvent>> sendEvent(
        final TransactionEvent transactionEvent
    ) {
        final ListenableFuture<SendResult<String, TransactionEvent>> sendTransactionEvent =
            kafkaTemplate.send(topic, transactionEvent);
        sendTransactionEvent.addCallback(
            new ListenableFutureCallback<SendResult<String, TransactionEvent>>() {

            @Override
            public void onSuccess(final SendResult<String, TransactionEvent> message) {
                log.info(
                    "process=sendTransactionEvent, message='{}', offset='{}'",
                    message,
                    message.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(final Throwable exception) {
                log.error("Unable to send message='{}'", transactionEvent.toString(), exception);
            }
        });

        return new CompletablePromise<>(sendTransactionEvent);
    }
}
