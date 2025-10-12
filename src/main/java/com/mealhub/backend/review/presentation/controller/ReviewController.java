package com.mealhub.backend.review.presentation.controller;

import com.mealhub.backend.review.application.service.ReviewService;
import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
import com.mealhub.backend.review.presentation.dto.request.ReviewUpdateDto;
import com.mealhub.backend.review.presentation.dto.response.PageResult;
import com.mealhub.backend.review.presentation.dto.response.ReviewListItemDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResDto> createReview(
            @RequestParam("r_id") UUID restaurantId,
            @RequestBody @Valid ReviewCreateDto createDto) {

        ReviewResDto res = reviewService.createReview(restaurantId, createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    @GetMapping("/{rv_id}")
    public ResponseEntity<ReviewResDto> getReview(@PathVariable("rv_id") UUID reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping
    public ResponseEntity<PageResult<ReviewListItemDto>> getListReviews(
            @RequestParam("r_id") UUID restaurantId,
            @RequestParam(name = "sort", defaultValue = "latest") String sort,
            @PageableDefault(size = 10) Pageable pageable // size=10 기본
    ) {
        var page = reviewService.getListReviews(restaurantId, sort, pageable);
        return ResponseEntity.ok(PageResult.of(page));
    }

    @PatchMapping
    public ResponseEntity<ReviewResDto> updateReview(@RequestBody @Valid ReviewUpdateDto reviewUpdateDto) {
        return ResponseEntity.ok(reviewService.updateReview(reviewUpdateDto));
    }

    @DeleteMapping("/{rv_id}")
    public ResponseEntity<ReviewResDto> deleteReview(@PathVariable("rv_id") UUID reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }
}