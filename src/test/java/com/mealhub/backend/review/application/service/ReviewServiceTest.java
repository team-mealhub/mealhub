//package com.mealhub.backend.review.application.service;
//
//import com.mealhub.backend.order.domain.entity.OrderInfo;
//import com.mealhub.backend.order.domain.enums.OrderStatus;
//import com.mealhub.backend.order.infrastructure.repository.OrderInfoRepository;
//import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
//import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
//import com.mealhub.backend.review.domain.entity.ReviewEntity;
//import com.mealhub.backend.review.domain.exception.*;
//import com.mealhub.backend.review.infrastructure.repository.ReviewRepository;
//import com.mealhub.backend.review.presentation.dto.request.ReviewCreateDto;
//import com.mealhub.backend.review.presentation.dto.request.ReviewUpdateDto;
//import com.mealhub.backend.review.presentation.dto.response.ReviewListItemDto;
//import com.mealhub.backend.review.presentation.dto.response.ReviewResDto;
//import com.mealhub.backend.user.domain.entity.User;
//import com.mealhub.backend.user.domain.enums.UserRole;
//import com.mealhub.backend.user.infrastructure.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceTest {
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    @Mock private ReviewRepository reviewRepository;
//    @Mock private UserRepository userRepository;
//    @Mock private RestaurantRepository restaurantRepository;
//    @Mock private OrderInfoRepository orderInfoRepository;
//
//    private User makeUser(long id) {
//        // 최소 필드만 세팅 가능한 정적 생성기가 없다고 가정 -> 스파이로 id만 세팅
//        User user = mock(User.class);
//        when(user.getId()).thenReturn(id);
//        when(user.getNickname()).thenReturn("닉"+id);
//        when(user.getUserId()).thenReturn("user-"+id);
//        return user;
//    }
//
//    private OrderInfo makeDeliveredOrder(Long userId, UUID restaurantId) {
//        OrderInfo order = OrderInfo.createOrder(userId, restaurantId, UUID.randomUUID(), "req");
//        // 상태를 DELIVERED까지 진행
//        order.updateStatus(OrderStatus.IN_PROGRESS, "start");
//        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY, "ship");
//        order.updateStatus(OrderStatus.DELIVERED, "done");
//        return order;
//    }
//
//    @Nested
//    class CreateReview {
//
//        @Test
//        @DisplayName("리뷰 생성 성공")
//        void create_success() {
//            // given
//            UUID orderId = UUID.randomUUID();
//            UUID restaurantId = UUID.randomUUID();
//            Long userId = 10L;
//            UserRole role = UserRole.ROLE_CUSTOMER;
//
//            User user = makeUser(userId);
//            OrderInfo order = makeDeliveredOrder(userId, restaurantId);
//
//            when(userRepository.existsById(userId)).thenReturn(true);
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(orderInfoRepository.findByOInfoIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(order));
//            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(mock(RestaurantEntity.class)));
//            when(reviewRepository.existsByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(false);
//
//            ReviewCreateDto dto = new ReviewCreateDto((short)5, "맛있어요", false);
//
//            ReviewEntity saved = ReviewEntity.from(user, mock(RestaurantEntity.class), orderId, (short)5, "맛있어요", false);
//            when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(saved);
//
//            // when
//            ReviewResDto res = reviewService.createReview(orderId, dto, userId, role);
//
//            // then
//            assertThat(res.getStar()).isEqualTo(5);
//            assertThat(res.getUserId()).isEqualTo(userId);
//            verify(reviewRepository).save(any(ReviewEntity.class));
//        }
//
//        @Test
//        @DisplayName("리뷰 생성 실패 - 주문자가 아님")
//        void create_fail_notOrderOwner() {
//            UUID orderId = UUID.randomUUID();
//            UUID restaurantId = UUID.randomUUID();
//            Long loginUser = 20L; // 주문자와 다름
//
//            User user = makeUser(loginUser);
//            OrderInfo order = makeDeliveredOrder(999L, restaurantId); // 다른 사람 주문
//
//            when(userRepository.existsById(loginUser)).thenReturn(true);
//            when(userRepository.findById(loginUser)).thenReturn(Optional.of(user));
//            when(orderInfoRepository.findByOInfoIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(order));
//
//            assertThrows(NotOrderOwnerException.class, () ->
//                    reviewService.createReview(orderId, new ReviewCreateDto((short)5,"", false), loginUser, UserRole.ROLE_CUSTOMER)
//            );
//        }
//
//        @Test
//        @DisplayName("리뷰 생성 실패 - 배달 완료 전")
//        void create_fail_notDelivered() {
//            UUID orderId = UUID.randomUUID();
//            UUID restaurantId = UUID.randomUUID();
//            Long userId = 1L;
//
//            User user = makeUser(userId);
//            OrderInfo order = OrderInfo.createOrder(userId, restaurantId, UUID.randomUUID(), null); // PENDING
//
//            when(userRepository.existsById(userId)).thenReturn(true);
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(orderInfoRepository.findByOInfoIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(order));
//
//            assertThrows(ReviewOnlyAfterDeliveredException.class, () ->
//                    reviewService.createReview(orderId, new ReviewCreateDto((short)4,"", false), userId, UserRole.ROLE_CUSTOMER)
//            );
//        }
//
//        @Test
//        @DisplayName("리뷰 생성 실패 - 이미 주문 리뷰 존재")
//        void create_fail_duplicateByOrder() {
//            UUID orderId = UUID.randomUUID();
//            UUID restaurantId = UUID.randomUUID();
//            Long userId = 1L;
//
//            User user = makeUser(userId);
//            OrderInfo order = makeDeliveredOrder(userId, restaurantId);
//
//            when(userRepository.existsById(userId)).thenReturn(true);
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(orderInfoRepository.findByOInfoIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(order));
//            when(reviewRepository.existsByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(true);
//
//            assertThrows(ReviewAlreadyExistsForOrderException.class, () ->
//                    reviewService.createReview(orderId, new ReviewCreateDto((short)5,"", false), userId, UserRole.ROLE_CUSTOMER)
//            );
//        }
//    }
//
//    @Nested
//    class GetReview {
//
//        @Test
//        @DisplayName("공개 리뷰 - 누구나 조회 가능")
//        void public_review_anyone() {
//            UUID reviewId = UUID.randomUUID();
//            User author = makeUser(1L);
//            ReviewEntity review = ReviewEntity.from(author, mock(RestaurantEntity.class), UUID.randomUUID(), (short)4, "굿", false);
//
//            when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
//
//            ReviewResDto res = reviewService.getReview(reviewId, null, null);
//            assertThat(res.getStar()).isEqualTo(4);
//        }
//
//        @Test
//        @DisplayName("비공개 리뷰 - 작성자 본인은 조회 가능")
//        void owner_only_by_author() {
//            UUID reviewId = UUID.randomUUID();
//            User author = makeUser(7L);
//            ReviewEntity review = ReviewEntity.from(author, mock(RestaurantEntity.class), UUID.randomUUID(), (short)3, "비공개", true);
//
//            when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
//
//            ReviewResDto res = reviewService.getReview(reviewId, 7L, UserRole.ROLE_CUSTOMER);
//            assertThat(res.isOwnerOnly()).isTrue();
//        }
//
//        @Test
//        @DisplayName("비공개 리뷰 - 매니저는 조회 가능")
//        void owner_only_by_manager() {
//            UUID reviewId = UUID.randomUUID();
//            ReviewEntity review = ReviewEntity.from(makeUser(2L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)5, "비공개", true);
//
//            when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
//
//            ReviewResDto res = reviewService.getReview(reviewId, null, UserRole.ROLE_MANAGER);
//            assertThat(res.getStar()).isEqualTo(5);
//        }
//
//        @Test
//        @DisplayName("비공개 리뷰 - 권한 없는 타인은 불가")
//        void owner_only_forbidden() {
//            UUID reviewId = UUID.randomUUID();
//            ReviewEntity review = ReviewEntity.from(makeUser(1L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)2, "숨김", true);
//
//            when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
//
//            assertThrows(NotAllowedToViewReviewException.class,
//                    () -> reviewService.getReview(reviewId, 999L, UserRole.ROLE_CUSTOMER));
//        }
//    }
//
//    @Nested
//    class GetListReviews {
//
//        @Test
//        @DisplayName("리스트 - 매니저는 전체 조회")
//        void list_manager_all() {
//            UUID rId = UUID.randomUUID();
//            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//            Page<ReviewEntity> page = new PageImpl<>(List.of(
//                    ReviewEntity.from(makeUser(1L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)5, "a", false),
//                    ReviewEntity.from(makeUser(2L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)4, "b", true)
//            ));
//            when(reviewRepository.findByRestaurant_RestaurantIdAndDeletedAtIsNull(eq(rId), any(Pageable.class)))
//                    .thenReturn(page);
//
//            var res = reviewService.getListReviews(rId, "latest", pageable, 1L, UserRole.ROLE_MANAGER);
//            assertThat(res.getContent()).hasSize(2);
//            assertThat(res.getContent().get(0)).isInstanceOf(ReviewListItemDto.class);
//        }
//
//        @Test
//        @DisplayName("리스트 - 비로그인은 공개만")
//        void list_anonymous_public_only() {
//            UUID rId = UUID.randomUUID();
//            Pageable pageable = PageRequest.of(0, 5);
//
//            Page<ReviewEntity> page = new PageImpl<>(List.of(
//                    ReviewEntity.from(makeUser(1L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)5, "a", false)
//            ));
//            when(reviewRepository.findByRestaurant_RestaurantIdAndDeletedAtIsNullAndOwnerOnlyFalse(eq(rId), any(Pageable.class)))
//                    .thenReturn(page);
//
//            var res = reviewService.getListReviews(rId, "latest", pageable, null, null);
//            assertThat(res.getContent()).hasSize(1);
//        }
//
//        @Test
//        @DisplayName("리스트 - 로그인 유저는 공개 + 자신의 비공개")
//        void list_logged_in_visible() {
//            UUID rId = UUID.randomUUID();
//            Pageable pageable = PageRequest.of(0, 5);
//
//            when(reviewRepository.findVisibleForUser(eq(rId), eq(77L), any(Pageable.class)))
//                    .thenReturn(new PageImpl<>(List.of(
//                            ReviewEntity.from(makeUser(77L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)3, "mine", true),
//                            ReviewEntity.from(makeUser(1L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)5, "pub", false)
//                    )));
//
//            var res = reviewService.getListReviews(rId, "latest", pageable, 77L, UserRole.ROLE_CUSTOMER);
//            assertThat(res.getContent()).hasSize(2);
//        }
//    }
//
//    @Nested
//    class UpdateDelete {
//
//        @Test
//        @DisplayName("리뷰 수정 - 작성자 성공")
//        void update_by_author() {
//            Long authorId = 5L;
//            User author = makeUser(authorId);
//            ReviewEntity review = ReviewEntity.from(author, mock(RestaurantEntity.class), UUID.randomUUID(), (short)2, "old", false);
//
//            when(userRepository.existsById(authorId)).thenReturn(true);
//            when(reviewRepository.findByIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.of(review));
//
//            ReviewUpdateDto dto = new ReviewUpdateDto(UUID.randomUUID(), (short)5, "new", true);
//
//            ReviewResDto res = reviewService.updateReview(dto, authorId, UserRole.ROLE_CUSTOMER);
//            assertThat(res.getStar()).isEqualTo(5);
//            assertThat(res.getComment()).isEqualTo("new");
//            assertThat(res.isOwnerOnly()).isTrue();
//        }
//
//        @Test
//        @DisplayName("리뷰 삭제 - 매니저 가능")
//        void delete_by_manager() {
//            UUID rvId = UUID.randomUUID();
//            ReviewEntity review = ReviewEntity.from(makeUser(1L), mock(RestaurantEntity.class), UUID.randomUUID(), (short)4, "c", false);
//
//            when(userRepository.existsById(999L)).thenReturn(true);
//            when(reviewRepository.findByIdAndDeletedAtIsNull(rvId)).thenReturn(Optional.of(review));
//
//            ReviewResDto res = reviewService.deleteReview(rvId, 999L, UserRole.ROLE_MANAGER);
//            assertThat(res.getReviewId()).isEqualTo(review.getId());
//        }
//    }
//}
