package com.github.rxjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ObservableGenerate1 {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservableGenerate1.class);

    private static int square(int a, int attempt) {
        if (a % 2 == 0 && attempt == 1) {
            throw new RuntimeException("FAIL: failure for even numbers");
        }
        return a * a;
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger i = new AtomicInteger(0);
        Flowable<Integer> numbers = Flowable.generate(emitter -> {
            emitter.onNext(i.incrementAndGet());
            if (i.get() > 50) {
                emitter.onComplete();
            }
        });

        Disposable disp = numbers.flatMapIterable(f -> Arrays.asList(f, f))
                .delay(100, TimeUnit.MILLISECONDS)
                .flatMap(n ->
                        Flowable.fromCallable(() -> square(n, 1))
                                .doOnError(th -> LOGGER.error("square error for {}", n, th.getMessage()))
                                .onErrorResumeNext(th -> Flowable.just(square(n, 2))))
                .doOnNext(v -> LOGGER.info("item {}", v))
                .doOnError(th -> LOGGER.error("error", th))
                .doOnComplete(() -> {
                    latch.countDown();
                })
                .subscribe();
        latch.await();
        disp.dispose();
    }
}
