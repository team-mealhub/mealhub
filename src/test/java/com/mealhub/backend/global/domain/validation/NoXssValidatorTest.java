package com.mealhub.backend.global.domain.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("XSS 검증 테스트")
class NoXssValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("정상 텍스트 - 검증 통과")
    void validText_Success() {
        // given
        TestDto dto = new TestDto("안녕하세요. 맛있는 음식입니다.");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("<script> 태그 - 검증 실패")
    void scriptTag_Failed() {
        // given
        TestDto dto = new TestDto("<script>alert('XSS')</script>");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("XSS 공격 위험이 있는 문자가 포함되어 있습니다");
    }

    @Test
    @DisplayName("javascript: 프로토콜 - 검증 실패")
    void javascriptProtocol_Failed() {
        // given
        TestDto dto = new TestDto("javascript:alert('XSS')");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("onclick 이벤트 - 검증 실패")
    void onclickEvent_Failed() {
        // given
        TestDto dto = new TestDto("<img onclick=alert('XSS')>");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("<iframe> 태그 - 검증 실패")
    void iframeTag_Failed() {
        // given
        TestDto dto = new TestDto("<iframe src='malicious.com'></iframe>");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("eval() 함수 - 검증 실패")
    void evalFunction_Failed() {
        // given
        TestDto dto = new TestDto("eval(maliciousCode)");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("null 값 - 검증 통과")
    void nullValue_Success() {
        // given
        TestDto dto = new TestDto(null);

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열 - 검증 통과")
    void emptyString_Success() {
        // given
        TestDto dto = new TestDto("");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("HTML 엔티티 포함 텍스트 - 검증 통과")
    void htmlEntities_Success() {
        // given
        TestDto dto = new TestDto("가격은 5,000원입니다. &amp; 맛있어요!");

        // when
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }

    static class TestDto {
        @NoXss
        private final String text;

        public TestDto(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
