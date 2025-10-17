package com.mealhub.backend.payment.presentation.controller;

import com.mealhub.backend.global.presentation.dto.PageResult;
import com.mealhub.backend.payment.application.service.PaymentService;
import com.mealhub.backend.payment.presentation.dto.request.PaymentLogRequest;
import com.mealhub.backend.payment.presentation.dto.response.PaymentLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제 API", description = "결제 내역 조회 기능 제공")
@RestController
@RequestMapping("/v1/payment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 내역 조회",
            description = "결제 내역을 조회합니다. (MANAGER 권한 필요)"
    )
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호", in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY)
    })
    @ApiResponse(responseCode = "200", description = "결제 내역 조회 성공")
    @PostMapping("/logs")
    public PageResult<PaymentLogResponse> getPaymentLogs(
            @Valid @RequestBody PaymentLogRequest.Search request,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return paymentService.getPaymentLogs(request, page, size);
    }
}
