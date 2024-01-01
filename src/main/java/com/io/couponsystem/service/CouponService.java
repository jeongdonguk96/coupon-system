package com.io.couponsystem.service;

import com.io.couponsystem.producer.CouponCreateProducer;
import com.io.couponsystem.repository.AppliedMemberRepository;
import com.io.couponsystem.repository.CouponCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedMemberRepository appliedMemberRepository;

    public void apply(Long memberId) {
        // redis에서 set 구조를 이용해 쿠폰 발급을 1인 1매로 제한한다.
        Long result = appliedMemberRepository.add(memberId);
        if (result != 1) {
            return;
        }

        // redis에서 요청 수를 증가해 최대 100개까지 받는다.
        Long count = couponCountRepository.increase();
        if (count > 100) {
            return;
        }

        // 토픽으로 데이터를 발행한다.
        couponCreateProducer.create(memberId);
    }
}
