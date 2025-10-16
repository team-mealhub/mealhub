//package com.mealhub.backend.review.presentation.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mealhub.backend.review.application.service.ReviewService;
//import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
//import com.mealhub.backend.review.presentation.dto.request.ReviewUpdateDto;
//import com.mealhub.backend.review.presentation.dto.response.ReviewListItemDto;
//import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
//import com.mealhub.backend.user.domain.enums.UserRole;
//import com.mealhub.backend.user.libs.MockUser;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.*;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles({"default","test"})
//class ReviewControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//
//    @MockBean private ReviewService reviewService;
//
//    private static final String ENDPOINT = "/v1/review";
//
//    private String toJson(Object o) throws Exception {
//        return objectMapper.writeValueAsString(o);
//    }
//
//    private ReviewResDto sampleRes(UUID rvId, long userId, short star, String comment, boolean ownerOnly) {
//        return new ReviewResDto(
//                rvId,
//                userId,
//                "닉"+userId,
//                star,
//                comment,
//                LocalDateTime.now(),
//                userId,
//                null,
//                null,
//                ownerOnly
//        );
//    }
//
//    @Test
//    @DisplayName("리뷰 생성")
//    @MockUser(id = 1L, role = UserRole.ROLE_CUSTOMER)
//    void createReview() throws Exception {
//        UUID orderId = UUID.randomUUID();
//
//        ReviewCreateDto req = new ReviewCreateDto((short)5, "맛있어요", false);
//        ReviewResDto res = sampleRes(UUID.randomUUID(), 1L, (short)5, "맛있어요", false);
//
//        given(reviewService.createReview(eq(orderId), any(ReviewCreateDto.class), eq(1L), eq(UserRole.ROLE_CUSTOMER)))
//                .willReturn(res);
//
//        mockMvc.perform(post(ENDPOINT)
//                        .param("o_info_id", orderId.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(req)))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.rv_id").value(res.getReviewId().toString()))
//                .andExpect(jsonPath("$.u_id").value(1))
//                .andExpect(jsonPath("$.rv_star").value(5))
//                .andExpect(jsonPath("$.rv_comment").value("맛있어요"));
//    }
//
//    @Test
//    @DisplayName("리뷰 단건 조회")
//    void getReview() throws Exception {
//        UUID rvId = UUID.randomUUID();
//        ReviewResDto res = sampleRes(rvId, 77L, (short)4, "굿", false);
//
//        given(reviewService.getReview(eq(rvId), isNull(), isNull())).willReturn(res);
//
//        mockMvc.perform(get(ENDPOINT + "/{rv_id}", rvId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.rv_id").value(rvId.toString()))
//                .andExpect(jsonPath("$.rv_star").value(4))
//                .andExpect(jsonPath("$.rv_comment").value("굿"));
//    }
//
//    @Test
//    @DisplayName("가게 리뷰 리스트 조회")
//    void getListReviews() throws Exception {
//        UUID rId = UUID.randomUUID();
//
//        Page<ReviewListItemDto> page = new PageImpl<>(
//                List.of(
//                        new ReviewListItemDto(UUID.randomUUID(), 1L, "닉1", (short)5, "a"),
//                        new ReviewListItemDto(UUID.randomUUID(), 2L, "닉2", (short)4, "b")
//                ),
//                PageRequest.of(0, 10),
//                2
//        );
//
//        given(reviewService.getListReviews(eq(rId), eq("latest"), any(Pageable.class), isNull(), isNull()))
//                .willReturn(page);
//
//        mockMvc.perform(get(ENDPOINT)
//                        .param("r_id", rId.toString())
//                        .param("sort", "latest"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.length()").value(2))
//                .andExpect(jsonPath("$.page").value(0))
//                .andExpect(jsonPath("$.size").value(10))
//                .andExpect(jsonPath("$.totalElements").value(2));
//    }
//
//    @Test
//    @DisplayName("리뷰 수정")
//    @MockUser(id = 10L, role = UserRole.ROLE_CUSTOMER)
//    void updateReview() throws Exception {
//        UUID rvId = UUID.randomUUID();
//        ReviewUpdateDto req = new ReviewUpdateDto(rvId, (short)3, "수정", true);
//        ReviewResDto res = sampleRes(rvId, 10L, (short)3, "수정", true);
//
//        given(reviewService.updateReview(any(ReviewUpdateDto.class), eq(10L), eq(UserRole.ROLE_CUSTOMER)))
//                .willReturn(res);
//
//        mockMvc.perform(patch(ENDPOINT)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(req)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.rv_id").value(rvId.toString()))
//                .andExpect(jsonPath("$.rv_star").value(3))
//                .andExpect(jsonPath("$.owner_only").value(true));
//    }
//
//    @Test
//    @DisplayName("리뷰 삭제")
//    @MockUser(id = 99L, role = UserRole.ROLE_MANAGER)
//    void deleteReview() throws Exception {
//        UUID rvId = UUID.randomUUID();
//        ReviewResDto res = sampleRes(rvId, 1L, (short)4, "bye", false);
//
//        given(reviewService.deleteReview(eq(rvId), eq(99L), eq(UserRole.ROLE_MANAGER)))
//                .willReturn(res);
//
//        mockMvc.perform(delete(ENDPOINT + "/{rv_id}", rvId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.rv_id").value(rvId.toString()));
//    }
//}
