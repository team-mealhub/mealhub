package com.mealhub.backend.restaurant.application.service;

import static org.springframework.util.StringUtils.hasText;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.address.infrastructure.repository.AddressRepository;
import com.mealhub.backend.global.domain.exception.CommonException;
import com.mealhub.backend.global.domain.exception.ForbiddenException;
import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantCategoryRepository;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantResponse;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // 인증된 사용자 확인 메서드
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommonException("존재하지 않는 유저입니다.",
                        HttpStatus.BAD_REQUEST));
    }

    // 등록된 가게 확인 메서드
    private RestaurantEntity findRestaurant(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CommonException("존재하지 않는 가게입니다.",
                        HttpStatus.BAD_REQUEST));
    }

    // 등록된 주소 확인 및 소유자 확인 메서드
    private Address findAddressAndOwner(RestaurantRequest restaurantRequest,
            Long userId) {
        // 주소 존재 확인
        Address findAddress = addressRepository.findById(restaurantRequest.getAddressId())
                .orElseThrow(() -> new CommonException("존재하지 않는 주소입니다.",
                        HttpStatus.BAD_REQUEST));

        // 주소 소유자 확인
        if (!findAddress.getUser().getId().equals(userId)) {
            throw new CommonException("본인의 주소만 등록할 수 있습니다.", HttpStatus.FORBIDDEN);
        }

        return findAddress;
    }

    // 등록된 카테고리 확인 메서드
    private RestaurantCategoryEntity findCategory(RestaurantRequest restaurantRequest) {
        return restaurantCategoryRepository.findById(
                        restaurantRequest.getCategoryId())
                .orElseThrow(() -> new CommonException("존재하지 않는 카테고리입니다.",
                        HttpStatus.BAD_REQUEST));
    }

    // 가게 소유자 확인
    private void verifyOwner(RestaurantEntity restaurantEntity, Long userId) {
        if (!restaurantEntity.getUser().getId().equals(userId)) {
            throw new CommonException("본인의 가게만 수정할 수 있습니다.", HttpStatus.FORBIDDEN);
        }
    }

    // 가게 등록
    @Transactional
    public RestaurantResponse createRestaurant(
            RestaurantRequest restaurantRequest,
            Long userId,
            UserRole role
    ) {

        // 권한 확인 : 가게 주인 or 관리자만 생성 가능
        if (role.equals(UserRole.ROLE_CUSTOMER)) {
            throw new ForbiddenException();
        }

        // 인증된 사용자 확인
        User findUser = findUser(userId);

        // 등록된 주소 확인 및 소유자 확인
        Address findAddress = findAddressAndOwner(restaurantRequest,
                userId);

        // 등록된 카테고리 확인
        RestaurantCategoryEntity findCategory = findCategory(restaurantRequest);

        // RestaurantEntity 생성
        RestaurantEntity restaurantEntity = RestaurantEntity.of(restaurantRequest, findUser,
                findAddress, findCategory);

        // RestaurantEntity 저장
        RestaurantEntity save = restaurantRepository.save(restaurantEntity);

        return RestaurantResponse.from(save);
    }

    // 가게 단건 조회
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurant(UUID restaurantId) {

        // 가게 존재 확인
        RestaurantEntity restaurantEntity = findRestaurant(restaurantId);

        return RestaurantResponse.from(restaurantEntity);

    }

    // 가게 수정
    @Transactional
    public RestaurantResponse updateRestaurant(UUID restaurantId,
            RestaurantRequest restaurantRequest, Long userId, UserRole role) {

        // 권한 확인 : 가게 주인 or 관리자만 수정 가능
        if (role.equals(UserRole.ROLE_CUSTOMER)) {
            throw new ForbiddenException();
        }

        // 인증된 사용자 확인
        findUser(userId);

        // 가게 존재 확인
        RestaurantEntity restaurantEntity = findRestaurant(restaurantId);

        // 가게 소유자 확인
        verifyOwner(restaurantEntity, userId);

        // 등록된 주소 확인 및 소유자 확인
        Address address = findAddressAndOwner(restaurantRequest,
                userId);

        // 등록된 카테고리 확인
        RestaurantCategoryEntity category = findCategory(restaurantRequest);

        // 가게 정보 수정
        restaurantEntity.updateRestaurant(restaurantRequest, address, category);

        return RestaurantResponse.from(restaurantEntity);
    }

    // 가게 삭제
    @Transactional
    public void deleteRestaurant(UUID restaurantId, Long userId, UserRole role) {

        // 권한 확인 : 가게 주인 or 관리자만 삭제 가능
        if (role.equals(UserRole.ROLE_CUSTOMER)) {
            throw new ForbiddenException();
        }

        // 인증된 사용자 확인
        User findUser = findUser(userId);

        // 가게 존재 확인
        RestaurantEntity restaurantEntity = findRestaurant(restaurantId);

        // 가게 소유자 확인
        verifyOwner(restaurantEntity, userId);

        // 가게 삭제
        restaurantRepository.delete(restaurantEntity);
    }

    // 가게 검색
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> searchRestaurants(String keyword, int page, int size,
            String sortBy, boolean isAsc) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<RestaurantEntity> pageResult;

        if (hasText(keyword)) {
            String kw = keyword.trim();

            if ("createdAt".equals(sortBy)) {
                pageResult = isAsc
                        ? restaurantRepository.findByKeywordOrderByCreatedAtAsc(kw, pageable)
                        : restaurantRepository.findByKeywordOrderByCreatedAtDesc(kw, pageable);
            } else if ("updatedAt".equals(sortBy)) {
                pageResult = isAsc
                        ? restaurantRepository.findByKeywordOrderByUpdatedAtAtAsc(kw, pageable)
                        : restaurantRepository.findByKeywordOrderByUpdatedAtDesc(kw, pageable);
            } else {
                pageResult = isAsc
                        ? restaurantRepository.findByKeywordOrderByCreatedAtAsc(kw, pageable)
                        : restaurantRepository.findByKeywordOrderByCreatedAtDesc(kw, pageable);
            }

        } else {
            if ("createdAt".equals(sortBy)) {
                pageResult = isAsc
                        ? restaurantRepository.findAllByOrderByCreatedAtAsc(pageable)
                        : restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
            } else if ("updatedAt".equals(sortBy)) {
                pageResult = isAsc
                        ? restaurantRepository.findAllByOrderByUpdatedAtAsc(pageable)
                        : restaurantRepository.findAllByOrderByUpdatedAtDesc(pageable);
            } else {
                pageResult = isAsc
                        ? restaurantRepository.findAllByOrderByCreatedAtAsc(pageable)
                        : restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        return pageResult.map(RestaurantResponse::from);
    }

    // 가게 상태 변경
    @Transactional
    public RestaurantResponse changeRestaurantStatus(UUID restaurantId,
            RestaurantRequest restaurantRequest, Long userId,
            UserRole role) {

        // 권한 확인 : 가게 주인 or 관리자만 삭제 가능
        if (role.equals(UserRole.ROLE_CUSTOMER)) {
            throw new ForbiddenException();
        }

        // 인증된 사용자 확인
        User findUser = findUser(userId);

        // 가게 존재 확인
        RestaurantEntity restaurantEntity = findRestaurant(restaurantId);

        // 가게 소유자 확인
        verifyOwner(restaurantEntity, userId);

        // 가게 상태 변경
        restaurantEntity.changeStatus(restaurantRequest.getIsOpen());

        return RestaurantResponse.from(restaurantEntity);
    }
}