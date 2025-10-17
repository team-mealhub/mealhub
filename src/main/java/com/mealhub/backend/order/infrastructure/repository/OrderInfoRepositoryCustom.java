package com.mealhub.backend.order.infrastructure.repository;

import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderInfoRepositoryCustom {

    /**
     * 주문 검색 (동적 쿼리)
     * QueryDSL을 사용하여 null 파라미터를 안전하게 처리
     *
     * @param userId 사용자 ID (nullable)
     * @param restaurantIds 레스토랑 ID 리스트 (nullable)
     * @param status 주문 상태 (nullable)
     * @param from 검색 시작 일시 (nullable)
     * @param to 검색 종료 일시 (nullable)
     * @param pageable 페이징 정보
     * @return 검색 결과 페이지
     */
    Page<OrderInfo> searchOrders(
            Long userId,
            List<UUID> restaurantIds,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}
