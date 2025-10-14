package com.mealhub.backend.restaurant.application.service;

import com.mealhub.backend.global.domain.exception.ForbiddenException;
import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantCategoryRepository;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryPatchRequest;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantCategoryResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantCategoryService {

    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final RestaurantRepository restaurantRepository;

    // 등록된 가게 분류 확인 메서드
    private RestaurantCategoryEntity findCategory(UUID categoryId) {
        return restaurantCategoryRepository.findById(categoryId)
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
            RestaurantCategoryRequest restaurantCategoryRequest
    ) {

        // 가게 분류 중복 확인
        verifyDuplicateCategory(restaurantCategoryRequest.getCategory());

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
            RestaurantCategoryPatchRequest restaurantCategoryPatchRequest,
            UUID categoryId
    ) {

        // 등록된 가게 분류 확인
        RestaurantCategoryEntity category = findCategory(categoryId);

        // 가게 분류 중복 확인
        verifyDuplicateCategory(restaurantCategoryPatchRequest.getUpdatedCategory());

        // 가게 분류 수정
        category.updateCategory(restaurantCategoryPatchRequest);

        return RestaurantCategoryResponse.from(category);
    }

    // 가게 분류 삭제
    @Transactional
    public void deleteRestaurantCategory(UUID categoryId) {

        // 등록된 가게 분류 확인
        RestaurantCategoryEntity categoryEntity = findCategory(categoryId);

        // 해당 카테고리를 사용하는 가게가 있는지 확인
        List<RestaurantEntity> byCategory = restaurantRepository.findByCategory(categoryEntity);
        if (!byCategory.isEmpty()) {
            throw new ForbiddenException("해당 카테고리를 사용하는 가게가 존재합니다.");
        }

        // 가게 분류 삭제
        restaurantCategoryRepository.delete(categoryEntity);
    }
}