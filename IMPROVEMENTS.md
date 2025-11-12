# MealHub 개선 포인트

## 📋 목차
1. [개요](#개요)
2. [긴급 개선 사항](#긴급-개선-사항)
3. [성능 개선](#성능-개선)
4. [보안 강화](#보안-강화)
5. [코드 품질](#코드-품질)
6. [테스트 개선](#테스트-개선)
7. [아키텍처 개선](#아키텍처-개선)
8. [우선순위별 정리](#우선순위별-정리)

---

## 개요

본 문서는 MealHub 프로젝트의 코드베이스 분석을 통해 도출된 개선 포인트를 정리한 것입니다.

**분석 일자**: 2025-11-12
**분석 대상**: MealHub 백엔드 (Spring Boot 3.5.6, Java 17)
**프로젝트 규모**: 약 137개의 Java 파일, 9개의 도메인 모듈

---

## 긴급 개선 사항

### 1. Base Entity의 `@Data` 어노테이션 문제 🔴

**위치**: `BaseTimeEntity`, `BaseAuditEntity`

**문제점**:
- Lombok의 `@Data`는 `equals()`, `hashCode()`, `toString()`을 자동 생성
- JPA 엔티티에서 양방향 관계 시 `StackOverflowError` 발생 위험
- 잘못된 동등성 비교로 인한 데이터 무결성 문제 가능

**해결 방안**:
```java
// 현재
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity { ... }

// 개선
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    // equals(), hashCode()는 ID 기반으로 직접 구현
}
```

**영향도**: 전체 엔티티에 영향
**우선순위**: 🔴 High

---

### 2. Soft Delete 일관성 부족 🔴

**문제점**:
- `Address` 엔티티: `deleted` boolean + `deletedAt`/`deletedBy` 사용
- 다른 엔티티들: `deletedAt`/`deletedBy`만 사용
- 일부 엔티티에만 `@SQLRestriction` 적용

**해결 방안**:
1. 모든 soft delete 엔티티에 표준 패턴 적용
2. `@SQLRestriction("deleted_at IS NULL")` 추가
3. `deleted` boolean 플래그 제거하고 `deletedAt != null` 로직으로 통일

**영향 받는 엔티티**:
- `User`
- `Address`
- `Product`
- `OrderInfo`
- `ReviewEntity`

**우선순위**: 🔴 High

---

### 3. CI/CD 파이프라인에서 테스트 미실행 🔴

**위치**: `.github/workflows/build_test.yml`

**문제점**:
```yaml
- name: Build with Gradle
  run: ./gradlew build -x test  # 테스트를 건너뜀
```

**해결 방안**:
```yaml
- name: Build with Gradle
  run: ./gradlew build  # 테스트 포함
```

**우선순위**: 🔴 High

---

## 성능 개선

### 4. N+1 쿼리 문제 🟡

**문제점**:
- 대부분의 엔티티가 `FetchType.LAZY` 사용 (좋음)
- 하지만 `JOIN FETCH` 또는 `@EntityGraph` 활용 부족
- 관계 엔티티 조회 시 추가 쿼리 발생 가능

**해결 방안**:
```java
// 예시: OrderInfo 조회 시 OrderItems도 함께 로드
@Query("SELECT o FROM OrderInfo o " +
       "LEFT JOIN FETCH o.orderItems " +
       "WHERE o.id = :id AND o.deletedAt IS NULL")
Optional<OrderInfo> findByIdWithItems(@Param("id") UUID id);

// 또는 @EntityGraph 사용
@EntityGraph(attributePaths = {"orderItems", "restaurant"})
Optional<OrderInfo> findById(UUID id);
```

**우선순위**: 🟡 Medium

---

### 5. RestaurantRepository의 중복 JPQL 쿼리 🟡

**위치**: `RestaurantRepository.java` (Lines 25-63)

**문제점**:
- 8개의 유사한 쿼리가 정렬 방식만 다름
- 코드 중복 및 유지보수 어려움

**해결 방안**:
```java
// QueryDSL 또는 동적 정렬 활용
@Query("SELECT r FROM RestaurantEntity r " +
       "JOIN FETCH r.user " +
       "WHERE r.restaurantName LIKE %:keyword% " +
       "AND r.deletedAt IS NULL")
Page<RestaurantEntity> findByKeyword(
    @Param("keyword") String keyword,
    Pageable pageable  // 정렬 정보 포함
);
```

**우선순위**: 🟡 Medium

---

### 6. 페이지 크기 검증 로직 개선 🟢

**위치**: `OrderController.validatePageSize()`

**문제점**:
- 하드코딩된 유효 크기 [10, 30, 50]
- 컨트롤러에 비즈니스 로직 존재

**해결 방안**:
```java
// 컨트롤러
@GetMapping
public Page<OrderResponseDto> getOrders(
    @PageableDefault(size = 10) Pageable pageable
) { ... }

// 또는 커스텀 Validator 사용
@ValidPageSize(allowedSizes = {10, 30, 50})
int pageSize;
```

**우선순위**: 🟢 Low

---

## 보안 강화

### 7. JWT 에러 처리 개선 🟡

**위치**: `JwtUtil.validateToken()`

**문제점**:
```java
public boolean validateToken(String token) {
    try {
        // JWT 검증 로직
        return true;
    } catch (Exception e) {
        return false;  // 모든 예외를 동일하게 처리
    }
}
```

**해결 방안**:
```java
public JwtValidationResult validateToken(String token) {
    try {
        // 검증 로직
        return JwtValidationResult.valid();
    } catch (ExpiredJwtException e) {
        return JwtValidationResult.expired();
    } catch (MalformedJwtException e) {
        return JwtValidationResult.malformed();
    } catch (SignatureException e) {
        return JwtValidationResult.invalidSignature();
    }
}
```

**우선순위**: 🟡 Medium

---

### 8. 인증 엔드포인트 Rate Limiting 부재 🔴

**문제점**:
- 로그인/회원가입 엔드포인트에 요청 제한 없음
- 브루트포스 공격에 취약

**해결 방안**:
```java
// Bucket4j 또는 Spring Security RateLimiter 사용
@RateLimiter(name = "auth", fallbackMethod = "rateLimitFallback")
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) { ... }

// application.yml
resilience4j.ratelimiter:
  instances:
    auth:
      limitForPeriod: 5
      limitRefreshPeriod: 1m
      timeoutDuration: 0
```

**우선순위**: 🔴 High

---

### 9. 환경 변수 관리 개선 🟡

**위치**: `Dockerfile`, GitHub Actions Secrets

**문제점**:
- Dockerfile에 환경 변수 구조 노출
- 민감 정보 관리 개선 필요

**해결 방안**:
- AWS Secrets Manager 또는 HashiCorp Vault 사용
- Spring Cloud Config Server 도입 검토

**우선순위**: 🟡 Medium

---

## 코드 품질

### 10. 엔티티 네이밍 컨벤션 불일치 🟢

**문제점**:
- 일부: `RestaurantEntity`, `ReviewEntity`, `AiEntity`
- 다른 일부: `User`, `Product`, `Address`, `OrderInfo`

**해결 방안**:
- 팀 내 컨벤션 결정 (Entity 접미사 사용 여부)
- 일관성 있게 리팩토링

**우선순위**: 🟢 Low

---

### 11. DTO 불변성 개선 🟢

**현재 상태**:
```java
@Data
public class UserSignUpRequest {
    private String username;
    private String password;
}
```

**개선 방안**:
```java
@Value  // 불변 객체
public class UserSignUpRequest {
    String username;
    String password;
}
```

**우선순위**: 🟢 Low

---

### 12. 매직 넘버 제거 🟡

**위치**:
- `OrderInfo.java:121` - 5분 취소 제한
- `CartItem.java:76` - 수량 1000 제한

**해결 방안**:
```java
// application.yml
mealhub:
  order:
    cancel-timeout-minutes: 5
  cart:
    max-quantity: 1000

// @ConfigurationProperties 사용
@ConfigurationProperties(prefix = "mealhub.order")
public class OrderProperties {
    private int cancelTimeoutMinutes = 5;
}
```

**우선순위**: 🟡 Medium

---

### 13. 예외 메시지 일관성 🟡

**문제점**:
- 일부: i18n 메시지 코드 사용 (`"Order.NotFound"`)
- AI 서비스: 하드코딩된 한글 메시지 (`"존재하지 않는 유저입니다."`)

**해결 방안**:
- 모든 예외 메시지를 `messages/errors.properties`로 통일

**우선순위**: 🟡 Medium

---

## 테스트 개선

### 14. 테스트 커버리지 부족 🔴

**현황**:
- 총 137개 Java 파일 중 15개 테스트 파일 (~11%)
- Controller 통합 테스트 없음
- Repository 테스트 없음

**목표**:
- 최소 60% 이상 코드 커버리지
- 핵심 비즈니스 로직 100% 커버

**해결 방안**:
```java
// Controller 통합 테스트 예시
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldCreateOrder() throws Exception {
        mockMvc.perform(post("/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
            .andExpect(status().isCreated());
    }
}

// Repository 테스트 예시
@DataJpaTest
class OrderInfoRepositoryTest {
    @Autowired
    private OrderInfoRepository repository;

    @Test
    void shouldFindByUserId() {
        // given, when, then
    }
}
```

**우선순위**: 🔴 High

---

## 아키텍처 개선

### 15. 트랜잭션 경계 검증 🟡

**문제점**:
- 일부 쓰기 작업에 `@Transactional` 누락 가능성
- 트랜잭션 범위 불명확

**해결 방안**:
- 모든 Service 메서드 리뷰
- 읽기 전용 메서드: `@Transactional(readOnly = true)`
- 쓰기 메서드: `@Transactional`

**우선순위**: 🟡 Medium

---

### 16. 이벤트 핸들러 에러 처리 🔴

**위치**: `OrderEventListener`

**문제점**:
```java
@Async
@TransactionalEventListener
public void handleOrderCreated(OrderCreatedEvent event) {
    try {
        // 장바구니 업데이트
    } catch (Exception e) {
        log.error("Failed to update cart", e);
        // 에러를 삼켜버림 - 비즈니스 로직 실패가 무시됨
    }
}
```

**해결 방안**:
```java
// 1. 재시도 메커니즘 추가
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
public void handleOrderCreated(OrderCreatedEvent event) { ... }

// 2. Dead Letter Queue 도입
// 3. 실패 시 알림 발송 (Slack, Email)
// 4. 실패한 이벤트 DB에 저장하여 재처리 가능하도록
```

**우선순위**: 🔴 High

---

### 17. API 버저닝 전략 부재 🟢

**현재 상태**:
- `/v1/` 접두사 사용 중
- 버저닝 정책 문서화 부족

**해결 방안**:
- API 버저닝 정책 문서화
- 버전 폐기(deprecation) 프로세스 정의
- Breaking changes 처리 방법 명시

**우선순위**: 🟢 Low

---

### 18. 모니터링 및 관찰성(Observability) 부족 🟡

**문제점**:
- 분산 추적(Distributed Tracing) 없음
- 구조화된 로깅 부족
- 메트릭 수집 없음

**해결 방안**:
```yaml
# build.gradle
dependencies {
    // Actuator
    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    // Micrometer (Prometheus)
    implementation 'io.micrometer:micrometer-registry-prometheus'

    // Distributed Tracing (Zipkin)
    implementation 'io.micrometer:micrometer-tracing-bridge-brave'
    implementation 'io.zipkin.reporter2:zipkin-reporter-brave'
}

# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true
  tracing:
    sampling:
      probability: 1.0
```

**우선순위**: 🟡 Medium

---

## 우선순위별 정리

### 🔴 High Priority (즉시 처리 권장)

1. **Base Entity `@Data` 제거** - 데이터 무결성 위험
2. **Soft Delete 표준화** - 데이터 정합성 이슈
3. **CI/CD 테스트 활성화** - 품질 보증 필수
4. **Rate Limiting 구현** - 보안 취약점
5. **이벤트 핸들러 에러 처리** - 비즈니스 로직 무결성
6. **테스트 커버리지 향상** - 최소 60% 목표

### 🟡 Medium Priority (단기 개선)

7. **N+1 쿼리 최적화** - 성능 개선
8. **중복 쿼리 리팩토링** - 유지보수성
9. **JWT 에러 처리** - 사용자 경험 개선
10. **환경 변수 관리** - 보안 강화
11. **매직 넘버 제거** - 코드 가독성
12. **예외 메시지 표준화** - 일관성
13. **트랜잭션 검증** - 데이터 일관성
14. **모니터링 도입** - 운영 안정성

### 🟢 Low Priority (장기 개선)

15. **네이밍 컨벤션 통일** - 코드 스타일
16. **DTO 불변성** - 함수형 프로그래밍
17. **페이지 검증 개선** - 코드 품질
18. **API 버저닝 정책** - 문서화

---

## 프로젝트의 강점 ✨

개선점과 함께 프로젝트의 강점도 명시합니다:

1. ✅ **클린 아키텍처** - 명확한 계층 분리
2. ✅ **이벤트 드리븐 설계** - 주문 관리의 정교한 이벤트 시스템
3. ✅ **복잡한 비즈니스 로직** - 주문 상태 머신의 올바른 구현
4. ✅ **보안** - JWT 인증, 역할 기반 접근 제어
5. ✅ **API 문서화** - Swagger 통합
6. ✅ **Soft Delete 패턴** - 감사 추적 가능
7. ✅ **도메인 주도 설계** - 비즈니스 로직이 포함된 풍부한 도메인 모델
8. ✅ **최신 기술 스택** - Spring Boot 3.5.6, Java 17
9. ✅ **CI/CD 파이프라인** - 자동화된 배포
10. ✅ **국제화 지원** - 에러 메시지 다국어 처리

---

## 구현 순서 제안

### Phase 1: 긴급 수정 (1-2주)
- [ ] Base Entity `@Data` 제거
- [ ] CI/CD 테스트 활성화
- [ ] Rate Limiting 구현
- [ ] 이벤트 핸들러 에러 처리

### Phase 2: 테스트 강화 (2-3주)
- [ ] 핵심 비즈니스 로직 테스트 작성
- [ ] Controller 통합 테스트
- [ ] Repository 테스트
- [ ] 커버리지 60% 달성

### Phase 3: 성능 최적화 (2주)
- [ ] N+1 쿼리 해결
- [ ] 중복 쿼리 리팩토링
- [ ] 인덱스 최적화

### Phase 4: 운영 안정성 (2-3주)
- [ ] 모니터링 도입
- [ ] 구조화된 로깅
- [ ] 알림 시스템

### Phase 5: 코드 품질 (지속적)
- [ ] 매직 넘버 제거
- [ ] 네이밍 표준화
- [ ] 문서화 보완

---

## 결론

MealHub 프로젝트는 **탄탄한 아키텍처 기반**과 **현대적인 개발 관행**을 보여주는 잘 구성된 프로젝트입니다.

다만, 프로덕션 환경에서 안정적으로 운영하기 위해서는:
1. **데이터 무결성** 관련 긴급 이슈 해결
2. **테스트 커버리지** 대폭 향상
3. **보안 강화** (Rate Limiting, 에러 처리)
4. **운영 안정성** (모니터링, 로깅)

위 개선 사항들을 순차적으로 적용할 것을 권장합니다.

---

**작성일**: 2025-11-12
**분석자**: Claude Code
**버전**: 1.0
