package com.mealhub.backend.review.application.service;

import com.mealhub.backend.global.domain.exception.ForbiddenException;
import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.global.domain.exception.UnAuthorizedException;
import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.infrastructure.repository.OrderInfoRepository;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.review.domain.entity.ReviewEntity;
import com.mealhub.backend.review.infrastructure.repository.ReviewRepository;
import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
import com.mealhub.backend.review.presentation.dto.request.ReviewUpdateDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewListItemDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mealhub.backend.user.domain.enums.UserRole;

import static org.springframework.data.domain.Sort.Direction.*;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderInfoRepository orderInfoRepository;

    // 현재 user가 가게의 owner인지 확인
    private boolean isRestaurantOwner(Long currentUserId, UUID restaurantId) {
        if (currentUserId == null || restaurantId == null) {
            return false;
        }
        return restaurantRepository.existsByRestaurantIdAndUser_Id(restaurantId, currentUserId);
    }

    // 작성자 본인 또는 ROLE_MANAGER 권한이 있어야 통과
    private void checkReviewerOrManager(Long reviewerId, Long currentUserId, UserRole role) {
        if (Objects.equals(reviewerId, currentUserId)) return;
        if (role == null) throw new UnAuthorizedException("UNAUTHORIZED");
        if (role != UserRole.ROLE_MANAGER)
            throw new ForbiddenException("NOT_REVIEW_OWNER_OR_MANAGER");
    }

    @Transactional
    public ReviewResDto createReview(UUID orderId, ReviewCreateDto createDto, Long userId, UserRole role) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UnAuthorizedException("UNAUTHORIZED");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        OrderInfo order = orderInfoRepository.findByOInfoIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new NotFoundException("ORDER_NOT_FOUND"));

        // 주문자 = 현재 로그인한 사용자 확인
        if (!order.getUserId().equals(user.getId())) {
            throw new ForbiddenException("NOT_ORDER_OWNER");
        }

        // 배달이 완료된 주문만 허용
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new ForbiddenException("REVIEW_ALLOWED_ONLY_AFTER_DELIVERED");
        }

        // 주문에 저장된 가게로 매핑
        var restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("RESTAURANT_NOT_FOUND"));

        ReviewEntity savedReviewEntity = reviewRepository.save(
                ReviewEntity.from(user, restaurant, createDto.getStar(), createDto.getComment(), createDto.getOwnerOnly())
        );

        return ReviewResDto.from(savedReviewEntity);
    }

    @Transactional(readOnly = true)
    public ReviewResDto getReview(UUID reviewId, Long userId, UserRole role) {
        var review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new NotFoundException("REVIEW_NOT_FOUND"));

        // MANAGER -> 모든 리뷰 허용
        if (role == UserRole.ROLE_MANAGER) return ReviewResDto.from(review);

        // 모든 사용자 -> 공개 리뷰만 허용
        if (!review.isOwnerOnly()) return ReviewResDto.from(review);

        // 작성자/해당 가게 사장만 -> ownerOnly=true 리뷰 허용
        if (userId != null) {
            if (Objects.equals(review.getUser().getId(), userId)) return ReviewResDto.from(review);
            if (isRestaurantOwner(userId, review.getRestaurant().getRestaurantId())) return ReviewResDto.from(review);
        }
        throw new ForbiddenException("NOT_ALLOWED_TO_VIEW_REVIEW");
    }

    @Transactional(readOnly = true)
    public Page<ReviewListItemDto> getListReviews(UUID restaurantId, String sort, Pageable pageable, Long userId, UserRole role) {

        Sort baseSort = switch ((sort == null || sort.isBlank()) ? "latest" : sort) {
            case "rating_desc" -> Sort.by(DESC, "star");
            case "rating_asc" -> Sort.by(ASC, "star");
            default -> Sort.by(DESC, "createdAt");
        };

        Sort finalSort = pageable.getSort().isSorted() ? pageable.getSort() : baseSort;
        PageRequest pageReq = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), finalSort);

        // 매니저 or 해당 가게 사장 -> 전체 (ownerOnly=true 포함)
        if (role == UserRole.ROLE_MANAGER || isRestaurantOwner(userId, restaurantId)) {
            return reviewRepository
                    .findByRestaurant_RestaurantIdAndDeletedAtIsNull(restaurantId, pageReq)
                    .map(ReviewListItemDto::from);
        }

        // 비로그인 -> 공개만
        if (userId == null) {
            return reviewRepository
                    .findByRestaurant_RestaurantIdAndDeletedAtIsNullAndOwnerOnlyFalse(restaurantId, pageReq)
                    .map(ReviewListItemDto::from);
        }

        // 일반 로그인 -> 공개 + "내가 쓴 ownerOnly"
        return reviewRepository
                .findVisibleForUser(restaurantId, userId, pageReq)
                .map(ReviewListItemDto::from);
    }

    @Transactional
    public ReviewResDto updateReview(ReviewUpdateDto reviewUpdateDto, Long userId, UserRole role) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UnAuthorizedException("UNAUTHORIZED");
        }

        var review = reviewRepository.findByIdAndDeletedAtIsNull(reviewUpdateDto.getReviewId())
                .orElseThrow(() -> new NotFoundException("REVIEW_NOT_FOUND"));

        checkReviewerOrManager(review.getUser().getId(), userId, role);

        review.update(reviewUpdateDto.getStar(), reviewUpdateDto.getComment(), reviewUpdateDto.getOwnerOnly());

        return ReviewResDto.from(review);
    }

    @Transactional
    public ReviewResDto deleteReview(UUID reviewId, Long userId, UserRole role) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UnAuthorizedException("UNAUTHORIZED");
        }

        ReviewEntity review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new NotFoundException("REVIEW_NOT_FOUND"));

        checkReviewerOrManager(review.getUser().getId(), userId, role);

        review.softDelete(userId);
        return ReviewResDto.from(review);
    }

}
