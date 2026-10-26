package com.atoken.update.dingding.annotaion;

import com.atoken.update.dingding.annotaion.registrar.AlarmScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AlarmScannerRegistrar.class)
public @interface AlarmScan {
    String[] basePackages();

    Class<?> markerInterface() default Class.class;
}
