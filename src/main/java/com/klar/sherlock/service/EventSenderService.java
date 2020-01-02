package com.klar.sherlock.service;

import com.klar.sherlock.model.transaction.TransactionEvent;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface EventSenderService {
    CompletableFuture<SendResult<String, TransactionEvent>> sendEvent(TransactionEvent transactionEvent);
}
