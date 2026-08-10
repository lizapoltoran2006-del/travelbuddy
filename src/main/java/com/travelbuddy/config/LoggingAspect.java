package com.travelbuddy.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.travelbuddy.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info(" [AOP LOG] Начинается выполнение метода сервиса: " + methodName);
        Object result = joinPoint.proceed();
        log.info("✅ [AOP LOG] Метод " + methodName + " успешно отработал!");
       return result;
    }
}
