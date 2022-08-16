package com.bcsdlab.biseo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = {EnumValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enum {

    // error 메세지
    String message() default "올바르지 않은 값입니다.";
    // 관리할 그룹 지정
    Class<?>[] groups() default {};
    // 심각도. 잘 사용하지 않는다고 함
    Class<? extends Payload>[] payload() default {};
    // 검증 할 Enum 지정
    Class<? extends java.lang.Enum<?>> enumClass();
}
