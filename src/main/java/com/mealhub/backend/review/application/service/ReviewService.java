package com.mealhub.backend.review.application.service;

import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.review.domain.entity.ReviewEntity;
import com.mealhub.backend.review.infrastructure.repository.ReviewRepository;
import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewListItemDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new AccessDeniedException("UNAUTHORIZED"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND"));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("RESTAURANT_NOT_FOUND"));

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
                .orElseThrow(() -> new com.mealhub.backend.global.domain.exception.NotFoundException("REVIEW_NOT_FOUND"));
        return ReviewResDto.from(review);
    }

    public Page<ReviewListItemDto> getListReviews(UUID restaurantId, String sort, Pageable pageable) {

        Sort baseSort = switch ((sort == null || sort.isBlank()) ? "latest" : sort) {
            case "rating_desc" -> Sort.by(DESC, "star");
            case "rating_asc"  -> Sort.by(ASC,  "star");
            default            -> Sort.by(DESC, "createdAt");
        };

        Sort finalSort = pageable.getSort().isSorted() ? pageable.getSort() : baseSort;
        PageRequest pageReq = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), finalSort);

        return reviewRepository
                .findByRestaurant_RestaurantIdAndDeletedAtIsNull(restaurantId, pageReq)
                .map(ReviewListItemDto::from);
    }


}
