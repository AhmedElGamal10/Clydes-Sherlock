package com.example.demo.service;

import com.example.demo.model.transaction.TransactionEvent;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface EventSenderService {
    CompletableFuture<SendResult<String, TransactionEvent>> sendEvent(TransactionEvent transactionEvent);
}
