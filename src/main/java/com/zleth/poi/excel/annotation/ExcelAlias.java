package com.zleth.poi.excel.annotation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD , ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelAlias {

     String value() default "";
     HorizontalAlignment alignment() default HorizontalAlignment.LEFT;
}
