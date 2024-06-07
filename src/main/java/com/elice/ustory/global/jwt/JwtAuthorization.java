package com.elice.ustory.global.jwt;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Schema(hidden = true)
public @interface JwtAuthorization {

    boolean required() default true;
}
