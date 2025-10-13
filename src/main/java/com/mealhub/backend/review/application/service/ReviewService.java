package com.mealhub.backend.review.application.service;

import com.mealhub.backend.global.domain.exception.ForbiddenException;
import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.global.domain.exception.UnAuthorizedException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mealhub.backend.user.domain.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.springframework.data.domain.Sort.Direction.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AuditorAware<Long> auditorAware;

    @Transactional
    public ReviewResDto createReview(UUID restaurantId, ReviewCreateDto createDto) {
        Long currentUserId = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new UnAuthorizedException("UNAUTHORIZED"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("RESTAURANT_NOT_FOUND"));

        ReviewEntity reviewEntity = reviewRepository.save(
                ReviewEntity.from(user, restaurant, createDto.getStar(), createDto.getComment())
        );

        return new ReviewResDto(
                reviewEntity.getId(),
                reviewEntity.getUser().getId(),
                reviewEntity.getStar(),
                reviewEntity.getComment(),
                reviewEntity.getCreatedAt(),
                reviewEntity.getCreatedBy(),
                reviewEntity.getUpdatedAt(),
                reviewEntity.getUpdatedBy()
        );
    }

    public ReviewResDto getReview(UUID reviewId) {
        var review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new NotFoundException("REVIEW_NOT_FOUND"));
        return ReviewResDto.from(review);
    }

    public Page<ReviewListItemDto> getListReviews(UUID restaurantId, String sort, Pageable pageable) {

        Sort baseSort = switch ((sort == null || sort.isBlank()) ? "latest" : sort) {
            case "rating_desc" -> Sort.by(DESC, "star");
            case "rating_asc" -> Sort.by(ASC, "star");
            default -> Sort.by(DESC, "createdAt");
        };

        Sort finalSort = pageable.getSort().isSorted() ? pageable.getSort() : baseSort;
        PageRequest pageReq = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), finalSort);

        return reviewRepository
                .findByRestaurant_RestaurantIdAndDeletedAtIsNull(restaurantId, pageReq)
                .map(ReviewListItemDto::from);
    }

    @Transactional
    public ReviewResDto updateReview(ReviewUpdateDto reviewUpdateDto) {
        Long currentUserId = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new UnAuthorizedException("UNAUTHORIZED"));

        var review = reviewRepository.findByIdAndDeletedAtIsNull(reviewUpdateDto.getReviewId())
                .orElseThrow(() -> new NotFoundException("REVIEW_NOT_FOUND"));

        if (!review.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("NOT_REVIEW_OWNER");
        }

        review.update(reviewUpdateDto.getStar(), reviewUpdateDto.getComment());

        return ReviewResDto.from(review);
    }

    @Transactional
    public ReviewResDto deleteReview(UUID reviewId) {
        Long currentUserId = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException("UNAUTHORIZED"));

        ReviewEntity review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new NotFoundException("REVIEW_NOT_FOUND"));

        boolean isOwner = review.getUser().getId().equals(currentUserId);
        boolean isManager = hasManagerRole();

        if (!(isOwner || isManager)) {
            throw new ForbiddenException("NOT_REVIEW_OWNER_OR_MANAGER");
        }

        review.softDelete(currentUserId);
        return ReviewResDto.from(review);
    }

    // 작성자 본인 또는 ROLE_MANAGET 권한이 있어야 통과
    private void checkOwnerOrManager(Long ownerUserId, Long currentUserId) {
        boolean isOwner = ownerUserId.equals(currentUserId);
        boolean isManager = hasManagerRole();
        if (!(isOwner || isManager)) {
            throw new ForbiddenException("NOT_REVIEW_OWNER_OR_MANAGER");
        }
    }

    private boolean hasManagerRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        String need = UserRole.ROLE_MANAGER.name();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(need::equals);
    }

}
