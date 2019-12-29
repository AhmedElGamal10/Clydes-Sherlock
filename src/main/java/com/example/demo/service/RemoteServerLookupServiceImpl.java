package com.example.demo.service;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;

@Service
public class RemoteServerLookupServiceImpl implements RemoteServerLookupService {

    private final RestTemplate restTemplate;

    RateLimiter rateLimiter = RateLimiter.create(200);

    public RemoteServerLookupServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<User>> sendGetSystemUsersRequest() {
        rateLimiter.acquire();

        final String uri = "http://localhost:8081/clydescards.example.com/users";

        ResponseEntity<List<User>> getSystemUsersResponse =
                restTemplate.exchange(uri,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                        });
        List<User> users = getSystemUsersResponse.getBody();

        return CompletableFuture.completedFuture(users);
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Transaction>> sendGetUserTransactionsRequest(User user) {
        rateLimiter.acquire();

        String uri = buildUserTransactionsRequestPath(user);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Transaction>> getUserTransactionsResponse =
                restTemplate.exchange(uri,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Transaction>>() {
                        });

        List<Transaction> userTransactions = getUserTransactionsResponse.getBody();

        return CompletableFuture.completedFuture(userTransactions);
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
