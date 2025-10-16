package com.mealhub.backend.ai.presentation.controller;
/*
import com.mealhub.backend.ai.application.service.AiService;
import com.mealhub.backend.ai.presentation.dto.request.AiGenerateDescriptionRequest;
import com.mealhub.backend.ai.presentation.dto.response.AiGenerateDescriptionResponse;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ai Controller", description = "AI 도메인 API")
@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate-description")
    @Operation(summary = "AI 상품 설명 자동 생성")
    @ResponseStatus(HttpStatus.CREATED)
    public AiGenerateDescriptionResponse generateDescription(
            @Valid @RequestBody AiGenerateDescriptionRequest aiGenerateDescriptionRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        Long userId = userDetailsImpl.getId();

        return aiService.generateDescription(aiGenerateDescriptionRequest, userId);
    }
}
*/