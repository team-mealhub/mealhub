package com.mealhub.backend.order.infrastructure.repository;

import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.entity.QOrderInfo;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderInfoRepositoryImpl implements OrderInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderInfo> searchOrders(
            Long userId,
            List<UUID> restaurantIds,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        QOrderInfo orderInfo = QOrderInfo.orderInfo;

        // 동적 쿼리 작성
        JPAQuery<OrderInfo> query = queryFactory
                .selectFrom(orderInfo)
                .where(
                        userIdEq(userId),
                        restaurantIdsIn(restaurantIds),
                        statusEq(status),
                        createdAtGoe(from),
                        createdAtLoe(to),
                        orderInfo.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 적용
        pageable.getSort().forEach(order -> {
            if (order.getProperty().equals("createdAt")) {
                query.orderBy(order.isAscending() ?
                    orderInfo.createdAt.asc() : orderInfo.createdAt.desc());
            } else if (order.getProperty().equals("total")) {
                query.orderBy(order.isAscending() ?
                    orderInfo.total.asc() : orderInfo.total.desc());
            }
        });

        List<OrderInfo> content = query.fetch();

        // Count 쿼리 (페이징을 위한 전체 개수 조회)
        JPAQuery<Long> countQuery = queryFactory
                .select(orderInfo.count())
                .from(orderInfo)
                .where(
                        userIdEq(userId),
                        restaurantIdsIn(restaurantIds),
                        statusEq(status),
                        createdAtGoe(from),
                        createdAtLoe(to),
                        orderInfo.deletedAt.isNull()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 동적 조건 메서드들 (null 안전)
    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? QOrderInfo.orderInfo.userId.eq(userId) : null;
    }

    private BooleanExpression restaurantIdsIn(List<UUID> restaurantIds) {
        return (restaurantIds != null && !restaurantIds.isEmpty()) ?
            QOrderInfo.orderInfo.restaurantId.in(restaurantIds) : null;
    }

    private BooleanExpression statusEq(OrderStatus status) {
        return status != null ? QOrderInfo.orderInfo.status.eq(status) : null;
    }

    private BooleanExpression createdAtGoe(LocalDateTime from) {
        return from != null ? QOrderInfo.orderInfo.createdAt.goe(from) : null;
    }

    private BooleanExpression createdAtLoe(LocalDateTime to) {
        return to != null ? QOrderInfo.orderInfo.createdAt.loe(to) : null;
    }
}
