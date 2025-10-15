package com.mealhub.backend.user.libs;

import com.mealhub.backend.user.domain.enums.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface MockUser {
    long id() default 1L;
    String userId() default "test";
    String password() default "test1234!";
    UserRole role() default UserRole.ROLE_CUSTOMER;
}
