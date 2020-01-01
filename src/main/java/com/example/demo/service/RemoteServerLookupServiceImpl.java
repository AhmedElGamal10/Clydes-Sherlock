package com.example.demo.service;

import com.example.demo.AsyncEventsSystemRunner;
import com.example.demo.exception.RemoteServiceUnavailableException;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;
import static org.asynchttpclient.Dsl.*;

@Service
public class RemoteServerLookupServiceImpl implements RemoteServerLookupService {
    private final RestTemplate restTemplate;

    private static final Logger LOGGER = LogManager.getLogger(EventSenderServiceImpl.class);

    RateLimiter rateLimiter = RateLimiter.create(200);
    AsyncHttpClient asyncHttpClient = asyncHttpClient();

    AsyncHttpClient c = asyncHttpClient(config().setProxyServer(proxyServer("127.0.0.1", 38080)));
    ObjectMapper objectMapper = new ObjectMapper();
    public RemoteServerLookupServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<User>> getSystemUsers() {
        rateLimiter.acquire();
        final String uri = "http://localhost:8081/clydescards.example.com/users";
        Request request = get(uri).build();
        return asyncHttpClient.executeRequest(request).toCompletableFuture().thenApply(this::parseUsersResponse);
    }

    private List<User> parseUsersResponse(Response response) {
        try {
            return objectMapper.readValue(response.getResponseBody(), new TypeReference<List<User>>() {});
        } catch (JsonProcessingException e) {
            LOGGER.warn("Not able to parse the users response");
            return new LinkedList<>();
        }
    }

    private List<Transaction> parseTransactionsResponse(Response response) {
        try {
            return objectMapper.readValue(response.getResponseBody(), new TypeReference<List<Transaction>>() {});
        } catch (JsonProcessingException e) {
            LOGGER.warn("Not able to parse the transactions response");
            return new LinkedList<>();
        }
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Transaction>> getUserTransactions(User user) {
        rateLimiter.acquire();
        String uri = buildUserTransactionsRequestPath(user);
        Request request = get(uri).build();
        return asyncHttpClient.executeRequest(request).toCompletableFuture().thenApply(this::parseTransactionsResponse);
    }

    private String buildUserTransactionsRequestPath(User user) {
        final String baseUri = "http://localhost:8081/clydescards.example.com/transactions?userId=";

        String currentDate = getCurrentDate();
        String fiveDaysAgoDate = getPastDateByDifferenceInDays(5);

        StringBuilder sb = new StringBuilder();
        sb.append(baseUri);
        sb.append(user.getId());

        sb.append("&");
        sb.append("startDate=");
        sb.append(currentDate);

        sb.append("&");
        sb.append("endDate=");
        sb.append(fiveDaysAgoDate);

        return sb.toString();
    }
}
