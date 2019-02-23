package com.exloki.balancedfly.core.persist;

import com.exloki.balancedfly.core.persist.mappers.DefaultFieldMapper;
import com.exloki.balancedfly.core.persist.mappers.IFieldMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistedField {

    String path() default "";

    String[] comments() default {};

    Class<? extends IFieldMapper> mapper() default DefaultFieldMapper.class;
}
