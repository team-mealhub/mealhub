package com.mealhub.backend.ai.application.service;
/*
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.mealhub.backend.ai.domain.entity.AiEntity;
import com.mealhub.backend.ai.infrastructure.repository.AiRepository;
import com.mealhub.backend.ai.presentation.dto.request.AiGenerateDescriptionRequest;
import com.mealhub.backend.ai.presentation.dto.response.AiGenerateDescriptionResponse;
import com.mealhub.backend.global.domain.exception.BadRequestException;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantCategoryRepository;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor

public class AiService {

    private final UserRepository userRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final AiRepository aiRepository;

    @Value("${gemini.api.key}")
    private String geminiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    // AI 상품 설명 자동 생성 (OWNER, MANAGER 권한 필요)
    @Transactional
    public AiGenerateDescriptionResponse generateDescription(
            @Valid AiGenerateDescriptionRequest aiGenerateDescriptionRequest, Long userId) {

        // 등록된 유저 확인
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 유저입니다."));

        // 등록된 카테고리 확인
        restaurantCategoryRepository.findByCategory(
                        aiGenerateDescriptionRequest.getProductCategory())
                .orElseThrow(() -> new BadRequestException("존재하지 않는 카테고리입니다."));

        String aiRequest = "상품 이름: " + aiGenerateDescriptionRequest.getProductName() +
                ", 상품 카테고리: " + aiGenerateDescriptionRequest.getProductCategory() +
                "위 상품의 이름과 카테고리를 참고하여 한 문장으로 상품 설명을 작성해줘.";

        // AI 요청 생성
        GenerateContentResponse response;
        try (Client client = Client.builder().apiKey(geminiKey).build()) {

            response = client.models.generateContent(
                    geminiModel,
                    aiRequest,
                    null);
        }

        String aiResponse = response.text();

        // AI 응답 로그
        log.info("AI 응답: {}, {}", aiResponse, aiRequest);

        // AiEntity 생성
        AiEntity aiEntity = AiEntity.of(findUser, aiRequest, aiResponse);

        // AiEntity 저장
        AiEntity save = aiRepository.save(aiEntity);

        return AiGenerateDescriptionResponse.from(aiResponse);
    }
}
*/

