package com.icbc.sh.techmg.framework.log;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAccessLog {
    String value() default "";
}
