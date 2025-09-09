package com.sky.aspect;

import com.sky.annotaton.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
 * 自定义切面类
 *  切面= 通知+切入点
 * */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //1.定义切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) &&  @annotation(com.sky.annotaton.AutoFill)")
    public void autoFillCut() {
    }

    //2.定义通知
    //joinPoint：连接点 可以获取切入点的参数信息 切入点的方法名称
    @Before("autoFillCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始填充公共字段：{}", joinPoint);
        //1.获取被拦截的方法上的数据库操作类型
        //1.1 获取方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //1.2获取方法签名上的注解对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //1.3获取数据库操作类型
        OperationType operationType = annotation.value();
        //2.获取到当前方法上的参数-->实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

        //3.准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //4.根据不同的数据库操作类型，为对应的属性赋值
        if (operationType == OperationType.INSERT) {
            //赋值四个公共字段赋值
            try {
                //通过反射获取set方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //调用set方法
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            //为两个公共字段赋值
            try {
                //通过反射获取set方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //调用set方法
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
    }
}
