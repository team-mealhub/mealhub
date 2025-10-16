package com.mealhub.backend.payment.application.service;

import com.mealhub.backend.global.presentation.dto.PageResult;
import com.mealhub.backend.payment.domain.entity.PaymentLog;
import com.mealhub.backend.payment.domain.entity.QPaymentLog;
import com.mealhub.backend.payment.domain.repository.PaymentLogRepository;
import com.mealhub.backend.payment.presentation.dto.request.PaymentLogRequest;
import com.mealhub.backend.payment.presentation.dto.response.PaymentLogResponse;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentLogRepository paymentLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentLogResponse createPaymentLog(PaymentLogRequest.Create request) {
        PaymentLog paymentLog = PaymentLog.create(request);
        PaymentLog savedPaymentLog = paymentLogRepository.save(paymentLog);
        return new PaymentLogResponse(savedPaymentLog);
    }

    @Transactional(readOnly = true)
    public PageResult<PaymentLogResponse> getPaymentLogs(PaymentLogRequest.Search request, int page, int size) {
        size = List.of(10, 30, 50).contains(size) ? size : 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PaymentLog> paymentLogs = paymentLogRepository.findAll(buildSearchConditions(request), pageRequest);

        Page<PaymentLogResponse> pageResponse = paymentLogs.map(PaymentLogResponse::new);
        return PageResult.of(pageResponse);
    }

    private BooleanBuilder buildSearchConditions(PaymentLogRequest.Search request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.getUserId() != null) {
            builder.and(QPaymentLog.paymentLog.userId.eq(request.getUserId()));
        }

        if (request.getOrderId() != null) {
            builder.and(QPaymentLog.paymentLog.orderId.eq(request.getOrderId()));
        }

        if (request.getStatus() != null) {
            builder.and(QPaymentLog.paymentLog.status.eq(request.getStatus()));
        }

        return builder;
    }
}
