package com.io.couponsystem.service;

import com.io.couponsystem.domain.Coupon;
import com.io.couponsystem.repository.CouponCountRepository;
import com.io.couponsystem.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;

    public void apply(Long memberId) {
        Long count = couponCountRepository.increase();

        if (count > 100) {
            return;
        }

        couponRepository.save(new Coupon(memberId));
    }
}
