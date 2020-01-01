package com.example.demo.service;

import com.example.demo.exception.RemoteServiceUnavailableException;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;
import com.google.common.util.concurrent.RateLimiter;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.demo.util.DateUtils.getCurrentDate;
import static com.example.demo.util.DateUtils.getPastDateByDifferenceInDays;
import static org.asynchttpclient.Dsl.*;

@Service
public class RemoteServerLookupServiceImpl implements RemoteServerLookupService {
    private final RestTemplate restTemplate;

    RateLimiter rateLimiter = RateLimiter.create(200);

    public RemoteServerLookupServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

//    @Override
//    @Async("threadPoolTaskExecutor")
//    public CompletableFuture<List<User>> getSystemUsersOld() {
//        rateLimiter.acquire();
//
//        AsyncRestTemplate asycTemp = new AsyncRestTemplate();
//        String url ="http://google.com";
//        HttpMethod method = HttpMethod.GET;
//        Class<String> responseType = String.class;
//        //create request entity using HttpHeaders
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.TEXT_PLAIN);
//        HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
//        ListenableFuture<ResponseEntity<String>> future = asycTemp.exchange(url, method, requestEntity, responseType);
//        try {
//            //waits for the result
//            ResponseEntity<String> entity = future.get();
//            //prints body source code for the given URL
//            System.out.println(entity.getBody());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        final String uri = "http://localhost:8081/clydescards.example.com/users";
//        try {
//            ResponseEntity<List<User>> response =
//                    restTemplate.exchange(uri,
//                            HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
//                            });
//
//            List<User> responseAsList = response.getBody();
//
//            return CompletableFuture.completedFuture(responseAsList);
//        } catch (ResourceAccessException e) {
//            throw new RemoteServiceUnavailableException("Remote service is unavailable for call.");
//        }
//    }


//    AsyncHttpClient asyncHttpClient = asyncHttpClient(config().setProxyServer(proxyServer("127.0.0.1", 38080)));
    AsyncHttpClient asyncHttpClient = asyncHttpClient();
    AsyncHttpClient c = asyncHttpClient(config().setProxyServer(proxyServer("127.0.0.1", 38080)));

    @Override
    @Async("threadPoolTaskExecutor")
    public ListenableFuture<Response> getSystemUsers() {
        rateLimiter.acquire();
        final String uri = "http://localhost:8081/clydescards.example.com/users";
        Request request = get(uri).build();
        ListenableFuture<Response> whenResponse = asyncHttpClient.executeRequest(request);

        Runnable callback = () -> {
            try  {
                Response response = whenResponse.get();
                System.out.println(response);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        };

        return whenResponse;
    }



    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Transaction>> getUserTransactions(User user) {
        rateLimiter.acquire();
        String uri = buildUserTransactionsRequestPath(user);
        try {
            ResponseEntity<List<Transaction>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<Transaction>>() {
                            });

            List<Transaction> responseAsList = response.getBody();

            return CompletableFuture.completedFuture(responseAsList);
        } catch (ResourceAccessException e) {
            throw new RemoteServiceUnavailableException("Remote service is unavailable for call.");
        }
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
