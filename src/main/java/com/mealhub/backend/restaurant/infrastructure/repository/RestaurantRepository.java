package com.mealhub.backend.restaurant.infrastructure.repository;

import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

    // 전체 조회 (정렬)
    Page<RestaurantEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    Page<RestaurantEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<RestaurantEntity> findAllByOrderByUpdatedAtAsc(Pageable pageable);

    Page<RestaurantEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    // 키워드 LIKE 검색 (정렬) — 패턴은 JPQL에서 CONCAT('%', :keyword, '%')로 생성
    @Query("""
            SELECT r FROM RestaurantEntity r
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.createdAt ASC
            """)
    Page<RestaurantEntity> findByKeywordOrderByCreatedAtAsc(@Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            SELECT r FROM RestaurantEntity r
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.createdAt DESC
            """)
    Page<RestaurantEntity> findByKeywordOrderByCreatedAtDesc(@Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            SELECT r FROM RestaurantEntity r
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.updatedAt ASC
            """)
    Page<RestaurantEntity> findByKeywordOrderByUpdatedAtAtAsc(@Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            SELECT r FROM RestaurantEntity r
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.updatedAt DESC
            """)
    Page<RestaurantEntity> findByKeywordOrderByUpdatedAtDesc(@Param("keyword") String keyword,
            Pageable pageable);

    /**
     * Find all restaurants owned by a specific user
     *
     * @param userId the user ID
     * @return list of restaurants owned by the user
     */
    List<RestaurantEntity> findByUser_Id(Long userId);

    /**
     * Find restaurant by ID with User (FETCH JOIN to prevent N+1)
     *
     * @param restaurantId the restaurant ID
     * @return Optional of RestaurantEntity with User eagerly loaded
     */
    @Query("SELECT r FROM RestaurantEntity r JOIN FETCH r.user WHERE r.restaurantId = :restaurantId")
    java.util.Optional<RestaurantEntity> findByIdWithUser(@Param("restaurantId") UUID restaurantId);

    // 카테고리로 가게 조회
    List<RestaurantEntity> findByCategory(RestaurantCategoryEntity categoryEntity);
}