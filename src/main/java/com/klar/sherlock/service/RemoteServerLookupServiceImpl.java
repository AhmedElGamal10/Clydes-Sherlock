package com.klar.sherlock.service;

import com.klar.sherlock.model.transaction.Transaction;
import com.klar.sherlock.model.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.klar.sherlock.util.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.asynchttpclient.Dsl.*;

@Service
public class RemoteServerLookupServiceImpl implements RemoteServerLookupService {
    private static final Logger LOGGER = LogManager.getLogger(RemoteServerLookupService.class);

    RateLimiter rateLimiter = RateLimiter.create(200);
    AsyncHttpClient asyncHttpClient = asyncHttpClient();

    AsyncHttpClient client = asyncHttpClient(config().setProxyServer(proxyServer("127.0.0.1", 38080)));
    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${remote.service.uri}")
    private String remoteServiceUri;

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<User>> getSystemUsers() {
        rateLimiter.acquire();
        final String uri = remoteServiceUri + "/users";
        Request request = get(uri).build();

        return asyncHttpClient.executeRequest(request).toCompletableFuture().thenApply(this::parseUsersResponse);
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Transaction>> getUserTransactions(User user) {
        rateLimiter.acquire();
        String uri = buildUserTransactionsRequestPath(user);
        Request request = get(uri).build();
        return asyncHttpClient.executeRequest(request).toCompletableFuture().thenApply(this::parseTransactionsResponse);
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

    private String buildUserTransactionsRequestPath(User user) {
        final String baseUri = remoteServiceUri + "/transactions?userId=";

        String currentDate = DateUtils.getCurrentDate();
        String fiveDaysAgoDate = DateUtils.getPastDateByDifferenceInDays(5);

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
