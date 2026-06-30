package com.icbc.sh.techmg.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /** 幂等键前缀 */
    String value() default "";
    /** 过期时间 */
    long expire() default 5;
    /** 时间单位 */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
