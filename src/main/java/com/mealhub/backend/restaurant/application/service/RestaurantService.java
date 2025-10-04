package com.mealhub.backend.restaurant.application.service;

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
import lombok.RequiredArgsConstructor;
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

    // 가게 등록
    @Transactional
    public RestaurantResponse createRestaurant(
            RestaurantRequest restaurantRequest,
            String userId, // TODO : userId 타입 변경 예정 (String -> Long)
            UserRole role
    ) {
        // 권한 확인 : 가게 주인 or 관리자만 생성 가능
        if (role.equals(UserRole.ROLE_CUSTOMER)) {
            throw new ForbiddenException();
        }

        // RestaurantEntity 생성
        User findUser = findUser(1L); // TODO : userId로 변경

        Address findAddress = addressRepository.findById(restaurantRequest.getAddressId())
                .orElseThrow(() -> new CommonException("존재하지 않는 주소입니다.",
                        HttpStatus.BAD_REQUEST));

        RestaurantCategoryEntity findCategory = restaurantCategoryRepository.findById(
                        restaurantRequest.getCategoryId())
                .orElseThrow(() -> new CommonException("존재하지 않는 카테고리입니다.",
                        HttpStatus.BAD_REQUEST));

        RestaurantEntity restaurantEntity = RestaurantEntity.of(restaurantRequest, findUser,
                findAddress, findCategory);

        // RestaurantEntity 저장
        RestaurantEntity save = restaurantRepository.save(restaurantEntity);

        return RestaurantResponse.from(save);
    }
}
