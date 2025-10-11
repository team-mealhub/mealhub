package com.mealhub.backend.review.presentation.controller;

import com.mealhub.backend.review.application.service.ReviewService;
import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(
            @RequestParam("r_id") UUID restaurantId,
            @RequestBody ReviewCreateDto createDto) {

        ReviewResDto res = reviewService.createReview(restaurantId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "리뷰 저장 성공", "data", res));
    }
}