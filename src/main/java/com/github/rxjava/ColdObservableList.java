package com.github.rxjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ColdObservableList {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColdObservableList.class);

    private static int square(int a, int attempt) {
        if (a % 2 == 0 && attempt == 1) {
            throw new RuntimeException("FAIL: failure for even numbers");
        }
        return a * a;
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(2);
        final AtomicInteger i = new AtomicInteger(0);
        Observable<String> clubs = Observable.just("Man Utd", "Arsenal", "Man City", "Everton", "Southampton");
        Disposable disp1 =
                clubs
                        .doOnNext(value -> System.out.println("Ob 1 " + value))
                        .doOnComplete(() -> {
                            latch.countDown();
                        })
                        .subscribe();
        Disposable disp2 =
                clubs
                        .filter(p ->p.contains(" "))
                        .doOnNext(value -> System.out.println("Ob 2 " + value))
                        .doOnComplete(() -> {
                            latch.countDown();
                        })
                        .subscribe();

        latch.await();
        disp1.dispose();
        disp2.dispose();
    }
}
