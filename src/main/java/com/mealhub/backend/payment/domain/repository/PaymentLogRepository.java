package com.mealhub.backend.payment.domain.repository;

import com.mealhub.backend.payment.domain.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID>, QuerydslPredicateExecutor<PaymentLog> {
}
