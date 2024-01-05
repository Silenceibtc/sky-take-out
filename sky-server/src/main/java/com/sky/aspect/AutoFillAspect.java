package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    //切点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    //前置通知
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充...");

        //获取方法类型，是insert还是update
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationType type = signature.getMethod().getAnnotation(AutoFill.class).value();

        //获得方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0)
            return;
        //约定，要填充公共字段的实体对象放在参数第一个
        Object entity = args[0];

        //获得公共字段
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同类型，将公共字段写入实体
        if (type == OperationType.INSERT) {
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, LocalDateTime.now());
                setUpdateTime.invoke(entity, LocalDateTime.now());
                setCreateUser.invoke(entity, BaseContext.getCurrentId());
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, LocalDateTime.now());
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
