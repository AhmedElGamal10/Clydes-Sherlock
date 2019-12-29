package com.example.demo.service;

import com.example.demo.model.transaction.TransactionEvent;

public interface EventSenderService {
    void sendEvent(TransactionEvent transactionEvent);
}
