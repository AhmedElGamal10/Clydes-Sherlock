//package com.example.demo.util;
//
//import com.example.demo.model.user.User;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//
//import static com.example.demo.configuration.SpringAsyncConfig.getAsyncExecutor;
//
//public class StreamUtils {
//    public static <T> Stream<T> completionOrder(Collection<CompletableFuture<T>> futures) {
//        return StreamSupport.stream(new CompletionOrderSpliterator<>(futures), false);
//    }
//
//    public void stream() {
//        List<CompletableFuture<User>> futures = Stream
//                .map(i -> CompletableFuture.supplyAsync(withRandomDelay(10), getAsyncExecutor()))
//                .collect(Collectors.toList());
//
//        completionOrder(futures).forEach(System.out::println);
//    }
//
//    private Supplier<Integer> withRandomDelay(Integer i) {
//        return () -> {
//            try {
//                Thread.sleep(ThreadLocalRandom.current()
//                        .nextInt(10000));
//            } catch (InterruptedException e) {
//                // ignore shamelessly, don't do this on production
//            }
//            return i;
//        };
//    }
//}
