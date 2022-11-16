package com.github.rxjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class FluxGenerate1 {
    private static final Logger LOGGER = LoggerFactory.getLogger(FluxGenerate1.class);

    private static int square(int a, int attempt) {
        if (a % 2 == 0 && attempt == 1) {
            throw new RuntimeException("FAIL: failure for even numbers");
        }
        return a * a;
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger i = new AtomicInteger(0);
        Flux<Integer> numbers = Flux.generate(emitter -> {
            emitter.next(i.incrementAndGet());
            if (i.get() > 50) {
                emitter.complete();
            }
        });

        Disposable disp = Flux.from(numbers)
                .flatMapIterable(f -> Arrays.asList(f, f))
                .delayElements(Duration.ofMillis(100))
                .flatMap(n ->
                        Mono.fromCallable(() -> square(n, 1))
                                .doOnError(th -> LOGGER.error("square error for {}", n, th.getMessage()))
                                .onErrorResume(th -> Mono.just(square(n, 2)))
                )
                .doOnNext(v -> LOGGER.info("item {}", v))
                .doOnError(th -> LOGGER.error("error", th))
                .doOnComplete(() -> {
                    latch.countDown();
                })
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(1000))
                )
                .subscribe();
        latch.await();
        disp.dispose();
    }
}
