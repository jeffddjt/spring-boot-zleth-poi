package com.zleth.poi.excel.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD , ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelIgnore {

}
