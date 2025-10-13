package com.mealhub.backend.review.presentation.controller;

import com.mealhub.backend.review.application.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

}