package com.atoken.update.dingding.annotaion;

import java.lang.annotation.*;

/**
 * @author dfg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Alarm {
    String value() default "";
}
