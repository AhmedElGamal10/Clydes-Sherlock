package com.clyde.sherlock.service;

import static org.asynchttpclient.Dsl.get;

import com.clyde.sherlock.model.transaction.Transaction;
import com.clyde.sherlock.model.user.User;
import com.clyde.sherlock.util.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemoteServerLookupServiceImpl implements RemoteServerLookupService {

    private static final int TIME_DIFFERENCE_IN_DAYS = 5;
    private static final String USER_TRANSACTIONS_ENDPOINT = "/transactions?userId=";
    private static final String USERS_ENDPOINT = "/users";

    private final AsyncHttpClient asyncHttpClient;
    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Value("${remote.service.uri}")
    private String remoteServiceUri;

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<User>> getSystemUsers() {
        rateLimiter.acquire();
        final String uri = remoteServiceUri + USERS_ENDPOINT;
        final Request request = get(uri).build();
        return asyncHttpClient.executeRequest(request)
            .toCompletableFuture()
            .thenApply(this::parseUsersResponse);
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Transaction>> getUserTransactions(final User user) {
        rateLimiter.acquire();
        final String uri = buildUserTransactionsRequestPath(user);
        final Request request = get(uri).build();
        return asyncHttpClient
            .executeRequest(request)
            .toCompletableFuture()
            .thenApply(this::parseTransactionsResponse);
    }

    private List<User> parseUsersResponse(final Response response) {
        try {
            return objectMapper.readValue(
                response.getResponseBody(),
                new TypeReference<List<User>>() {});
        } catch (final JsonProcessingException e) {
            log.warn("Not able to parse the users' response");
            return Collections.EMPTY_LIST;
        }
    }

    private List<Transaction> parseTransactionsResponse(final Response response) {
        try {
            return objectMapper.readValue(
                response.getResponseBody(),
                new TypeReference<List<Transaction>>() {});
        } catch (final JsonProcessingException e) {
            log.warn("Not able to parse the transactions' response");
            return Collections.EMPTY_LIST;
        }
    }

    private String buildUserTransactionsRequestPath(final User user) {
        final String baseUri = remoteServiceUri + USER_TRANSACTIONS_ENDPOINT;

        final String currentDate = DateUtils.getCurrentDate();
        final String fiveDaysAgoDate =
            DateUtils.getPastDateByDifferenceInDays(TIME_DIFFERENCE_IN_DAYS);

        final StringBuilder sb = new StringBuilder();
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
