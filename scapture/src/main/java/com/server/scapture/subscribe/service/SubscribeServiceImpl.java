package com.server.scapture.subscribe.service;

import com.server.scapture.domain.Subscribe;
import com.server.scapture.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService{
    private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);
    private final SubscribeRepository subscribeRepository;

    // 만료된 구독을 확인하고 삭제하는 메서드
    public void checkRole() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간보다 이전에 만료된 구독을 모두 조회
        List<Subscribe> expiredSubscriptions = subscribeRepository.findByEndDateBefore(now);

        // 만료된 구독 삭제
        for (Subscribe subscribe : expiredSubscriptions) {
            logger.info("Deleting expired subscription for user: {}", subscribe.getUser().getId());
            subscribeRepository.delete(subscribe);
        }
    }
}
