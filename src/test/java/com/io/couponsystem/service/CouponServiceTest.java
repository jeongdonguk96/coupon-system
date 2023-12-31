package com.io.couponsystem.service;

import com.io.couponsystem.repository.CouponRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponService couponService;

    @BeforeEach
    void 데이터삭제() {
        couponRepository.deleteAll();
    }

    @Test
    void 한번만응모() {
        couponService.apply(1L);
        long count = couponRepository.count();

        System.out.println("count = " + count);
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void 여러번응모() throws InterruptedException {
        int threadCount = 1000;

        // java.util.concurrent에서 제공하는 클래스
        // 32개의 스레드풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // java.util.concurrent에서 제공하는 클래스
        // 1000개의 작업이 끝나야 다음으로 진행할 수 있도록 하는 장치의 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        /*
            1. 반복문 내의 executorService.submit()는 비동기적으로 로직을 수행한다.
            2. 그래서 메인스레드는 반복문에서 내려와 로직을 끝내게 되는데,
            3. latch.await()이 있으므로 메인스레드는 이 모든 latch들의 작업이 끝날 때 까지 대기상태에 빠진다.
            4. 이후 count가 0에 다다르면 메인 스레드를 대기상태로부터 깨워 남은 로직을 수행하게 한다.
         */
        for (int i = 1; i <= 1000; i++) {
            long memberId = i;
            executorService.submit(() -> {
                try {
                    couponService.apply(memberId);
                } finally {

                    // 1000부터 줄여나가서 0이 되면 메인 스레드를 대기상태에서 해제한다.
                    latch.countDown();
                }
            });
        }

        // 메인 스레드를 대기상태로 전환한다.
        latch.await();

        Thread.sleep(10000);

        long count = couponRepository.count();
        System.out.println("count = " + count);
        assertThat(count).isEqualTo(100);
    }

    @Test
    void 일인일매응모() throws InterruptedException {
        int threadCount = 1000;

        // java.util.concurrent에서 제공하는 클래스
        // 32개의 스레드풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // java.util.concurrent에서 제공하는 클래스
        // 1000개의 작업이 끝나야 다음으로 진행할 수 있도록 하는 장치의 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        /*
            1. 반복문 내의 executorService.submit()는 비동기적으로 로직을 수행한다.
            2. 그래서 메인스레드는 반복문에서 내려와 로직을 끝내게 되는데,
            3. latch.await()이 있으므로 메인스레드는 이 모든 latch들의 작업이 끝날 때 까지 대기상태에 빠진다.
            4. 이후 count가 0에 다다르면 메인 스레드를 대기상태로부터 깨워 남은 로직을 수행하게 한다.
         */
        for (int i = 1; i <= 1000; i++) {
            long memberId = i;
            executorService.submit(() -> {
                try {
                    couponService.apply(1L);
                } finally {

                    // 1000부터 줄여나가서 0이 되면 메인 스레드를 대기상태에서 해제한다.
                    latch.countDown();
                }
            });
        }

        // 메인 스레드를 대기상태로 전환한다.
        latch.await();

        Thread.sleep(10000);

        long count = couponRepository.count();
        System.out.println("count = " + count);
        assertThat(count).isEqualTo(1);
    }

}