package com.mealhub.backend.review.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import com.mealhub.backend.global.presentation.dto.PageResult;
import com.mealhub.backend.review.application.service.ReviewService;
import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
import com.mealhub.backend.review.presentation.dto.request.ReviewUpdateDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewListItemDto;
import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
import com.mealhub.backend.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/review")
@Tag(name = "Review", description = "리뷰 도메인 API - 리뷰 생성, 리뷰 단건 조회, 리뷰 리스트 조회, 리뷰 수정, 리뷰 삭제")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "주문 ID(o_info_id)기반 가게 리뷰 생성")
    @ApiResponse(responseCode = "201", description = "리뷰 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping
    public ResponseEntity<ReviewResDto> createReview(
            @RequestParam("o_info_id") UUID orderId,
            @RequestBody @Valid ReviewCreateDto createDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();
        ReviewResDto res = reviewService.createReview(orderId, createDto, userId, role);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID(rv_id)로 단건 조회")
    @Parameters({
            @Parameter(name = "rv_id", description = "조회할 리뷰 ID", in = ParameterIn.PATH, required = true)
    })
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/{rv_id}")
    public ResponseEntity<ReviewResDto> getReview(
            @PathVariable("rv_id") UUID reviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Long userId = (userDetailsImpl == null ? null : userDetailsImpl.getId());
        UserRole role = (userDetailsImpl == null ? null : userDetailsImpl.getRole());
        return ResponseEntity.ok(reviewService.getReview(reviewId, userId, role));
    }

    @GetMapping
    @Operation(
            summary = "가게 리뷰 리스트 조회",
            description = """
    가게에 대한 리뷰 목록을 반환합니다. (page는 1-base)
    정렬: sortBy=createdAt|star, isAsc=true|false
    최신순: sortBy=createdAt&isAsc=false
    별점 높은순: sortBy=star&isAsc=false
    별점 낮은순: sortBy=star&isAsc=true
    """
    )
    @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공")
    public ResponseEntity<PageResult<ReviewListItemDto>> getListReviews(
            @RequestParam("r_id") UUID restaurantId,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy, // createdAt | star
            @RequestParam(name = "isAsc", defaultValue = "false") boolean isAsc,
            @RequestParam(name = "page", defaultValue = "1") int page,  // 1-base로 받음
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        Long userId = (userDetailsImpl == null ? null : userDetailsImpl.getId());
        UserRole role = (userDetailsImpl == null ? null : userDetailsImpl.getRole());

        // sort 화이트리스트(오타 방지)
        String key = "star".equalsIgnoreCase(sortBy) ? "star" : "createdAt";
        Sort dir = isAsc ? Sort.by(Sort.Direction.ASC, key) : Sort.by(Sort.Direction.DESC, key);
        dir = dir.and(Sort.by(Sort.Direction.DESC, "id")); // 안정 정렬

        // 3) 페이지네이션: 1-base -> 0-base 보정 + size 범위 가드(1~100)
        int safePage = Math.max(page - 1, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        PageRequest pageReq = PageRequest.of(safePage, safeSize, dir);

        var pageResult = reviewService.getListReviews(restaurantId, pageReq, userId, role);
        return ResponseEntity.ok(PageResult.of(pageResult));
    }

    @Operation(summary = "리뷰 수정", description = "별점/내용 리뷰 부분 수정")
    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PatchMapping
    public ResponseEntity<ReviewResDto> updateReview(
            @RequestBody @Valid ReviewUpdateDto reviewUpdateDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();
        return ResponseEntity.ok(reviewService.updateReview(reviewUpdateDto, userId, role));
    }

    @Operation(summary = "리뷰 삭제(Soft)", description = "리뷰를 소프트 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공")
    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping("/{rv_id}")
    public ResponseEntity<ReviewResDto> deleteReview(
            @PathVariable("rv_id") UUID reviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, userId, role));
    }

    @GetMapping("/my")
    @Operation(
            summary = "내가 작성한 리뷰 목록",
            description = """
    로그인 사용자가 작성한 리뷰 목록을 반환합니다. (page는 1-base)
    정렬: sortBy=createdAt|star, isAsc=true|false
    최신순: sortBy=createdAt&isAsc=false
    별점 높은순: sortBy=star&isAsc=false
    별점 낮은순: sortBy=star&isAsc=true
    """
    )
    public ResponseEntity<PageResult<ReviewListItemDto>> getMyReviews(
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "isAsc",  defaultValue = "false") boolean isAsc,
            @RequestParam(name = "page",   defaultValue = "1") int page,   // 1-base
            @RequestParam(name = "size",   defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        Long userId = (userDetailsImpl == null ? null : userDetailsImpl.getId());

        // sort 화이트리스트
        String key = "star".equalsIgnoreCase(sortBy) ? "star" : "createdAt";
        Sort dir = isAsc ? Sort.by(Sort.Direction.ASC, key) : Sort.by(Sort.Direction.DESC, key);
        dir = dir.and(Sort.by(Sort.Direction.DESC, "id"));

        // 1-base → 0-base 보정
        int safePage = Math.max(page - 1, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        PageRequest pageReq = PageRequest.of(safePage, safeSize, dir);
        var pageResult = reviewService.getMyReviews(pageReq, userId);
        return ResponseEntity.ok(PageResult.of(pageResult));
    }
}