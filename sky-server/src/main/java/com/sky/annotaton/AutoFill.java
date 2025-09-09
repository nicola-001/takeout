package com.sky.annotaton;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nicola
 * 1.自定义注解 AutoFill，用于标识需要进行公共字段自动填充的方法
 * 2.自定义切面类 AutoFiAspect，统一拦截加入了 AutoFill 注解的方法,统一拦截加入了 AutoFi 注解的方法，通过反射为公共字段赋值
 * 3.在 Mapper 的方法上加入 AutoFill 注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AutoFill {

    // 设置数据库操作类型
    OperationType value();
}
