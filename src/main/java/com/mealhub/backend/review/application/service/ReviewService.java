package com.mealhub.backend.review.application.service;

import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.review.domain.entity.ReviewEntity;
import com.mealhub.backend.review.infrastructure.repository.ReviewRepository;
import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

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
        var updated = reviewEntity.getUpdatedAt() != null ? reviewEntity.getUpdatedAt() : reviewEntity.getCreatedAt();
        return new ReviewResDto(
                reviewEntity.getId(),
                reviewEntity.getUser().getId(),
                reviewEntity.getStar(),
                reviewEntity.getComment(),
                reviewEntity.getCreatedAt(),
                updated
        );
    }

}
