package com.mealhub.backend.payment.presentation.controller;

import com.mealhub.backend.payment.application.service.PaymentService;
import com.mealhub.backend.payment.domain.enums.PaymentStatus;
import com.mealhub.backend.payment.presentation.dto.request.PaymentLogRequest;
import com.mealhub.backend.payment.presentation.dto.response.PaymentLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/logs")
    public Page<PaymentLogResponse> getPaymentLogs(
            @RequestBody PaymentLogRequest.Search request,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        System.out.println("page = " + page);
        System.out.println("size = " + size);
        return paymentService.getPaymentLogs(request, page, size);
    }
}
