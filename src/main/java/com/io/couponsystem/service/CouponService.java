package com.io.couponsystem.service;

import com.io.couponsystem.producer.CouponCreateProducer;
import com.io.couponsystem.repository.CouponCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;

    public void apply(Long memberId) {
        Long count = couponCountRepository.increase();
        if (count > 100) {
            return;
        }

        couponCreateProducer.create(memberId);
    }
}
