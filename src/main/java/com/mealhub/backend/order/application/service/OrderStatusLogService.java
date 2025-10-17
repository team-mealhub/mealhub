package com.mealhub.backend.order.application.service;

import com.mealhub.backend.order.domain.entity.OrderStatusLog;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.infrastructure.repository.OrderStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderStatusLogService {

    private final OrderStatusLogRepository orderStatusLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createOrderStatusLog(UUID orderId, Long userId, OrderStatus prevStatus, OrderStatus currStatus, String reason) {
        var orderStatusLog = OrderStatusLog.createLog(orderId, userId, prevStatus, currStatus, reason);
        orderStatusLogRepository.save(orderStatusLog);
    }
}
