package com.mealhub.backend.restaurant.application.service;

import com.mealhub.backend.global.domain.exception.ForbiddenException;
import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantCategoryRepository;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantCategoryResponse;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantCategoryService {

    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final UserRepository userRepository;

    // 권한 확인 메서드 : ROLE_CUSTOMER는 접근 불가
    private void verifyCustomerRole(UserRole role) {
        if (role.equals(UserRole.ROLE_CUSTOMER)) {
            throw new ForbiddenException("권한이 필요합니다.");
        }
    }

    // 권한 확인 메서드 : ROLE_OWNER는 접근 불가
    private void verifyOwnerRole(UserRole role) {
        if (role.equals(UserRole.ROLE_OWNER)) {
            throw new ForbiddenException("권한이 필요합니다.");
        }
    }

    // 인증된 사용자 확인 메서드
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
    }

    // 등록된 가게 분류 확인 메서드
    private RestaurantCategoryEntity findCategory(String category) {
        return restaurantCategoryRepository.findByCategory(category)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 가게 분류입니다."));
    }

    // 가게 분류 중복 확인 메서드
    private void verifyDuplicateCategory(String category) {
        restaurantCategoryRepository.findByCategory(category)
                .ifPresent(c -> {
                    throw new ForbiddenException("이미 존재하는 가게 분류입니다.");
                });
    }

    // 가게 분류 추가
    @Transactional
    public RestaurantCategoryResponse createRestaurantCategory(
            RestaurantCategoryRequest restaurantCategoryRequest,
            UserRole role, Long userId
    ) {

        // 권한 확인 : MANAGER만 생성 가능
        verifyCustomerRole(role);
        verifyOwnerRole(role);

        // 인증된 사용자 확인
        findUser(userId);

        // 가게 분류 중복 확인
        restaurantCategoryRepository.findByCategory(restaurantCategoryRequest.getCategory())
                .ifPresent(category -> {
                    throw new ForbiddenException("이미 존재하는 가게 분류입니다.");
                });

        // 가게 분류 추가
        RestaurantCategoryEntity restaurantCategory = RestaurantCategoryEntity.of(
                restaurantCategoryRequest);

        // 가게 분류 저장
        RestaurantCategoryEntity save = restaurantCategoryRepository.save(
                restaurantCategory);

        return RestaurantCategoryResponse.from(save);
    }

    // 가게 분류 전체 조회
    @Transactional(readOnly = true)
    public List<RestaurantCategoryResponse> getRestaurantCategories() {

        List<RestaurantCategoryEntity> categories = restaurantCategoryRepository.findAll();

        return RestaurantCategoryResponse.fromList(categories);
    }

    // 가게 분류 수정
    @Transactional
    public RestaurantCategoryResponse updateRestaurantCategory(
            @Valid RestaurantCategoryRequest restaurantCategoryRequest, UserRole role, Long userId
    ) {
        // 권한 확인 : MANAGER만 수정 가능
        verifyCustomerRole(role);
        verifyOwnerRole(role);

        // 인증된 사용자 확인
        findUser(userId);

        // 등록된 가게 분류 확인
        RestaurantCategoryEntity category = findCategory(
                restaurantCategoryRequest.getCategory());

        // 가게 분류 중복 확인
        verifyDuplicateCategory(restaurantCategoryRequest.getUpdatedCategory());

        // 가게 분류 수정
        category.updateCategory(restaurantCategoryRequest);

        return RestaurantCategoryResponse.from(category);
    }
}