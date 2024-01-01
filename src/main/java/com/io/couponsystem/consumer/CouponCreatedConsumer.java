package com.io.couponsystem.consumer;

import com.io.couponsystem.domain.Coupon;
import com.io.couponsystem.domain.FailedEvent;
import com.io.couponsystem.repository.CouponRepository;
import com.io.couponsystem.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long memberId) {
        try {
            couponRepository.save(new Coupon(memberId));
        } catch (Exception e) {
            log.error("failed to create coupon. memberId = {}", memberId);
            failedEventRepository.save(new FailedEvent(memberId));
        }
    }
}
