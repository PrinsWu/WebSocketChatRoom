package com.prins.wschatroom.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/20
 */
@Aspect
@Configuration
@Slf4j
public class ControllerAspect {

    @Around("execution(* com.prins.springboot.demo.web.controller.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info(String.format("before method{%s} Object{%s}", joinPoint.getSignature(), joinPoint.getTarget()));
        Object obj = joinPoint.proceed();
        log.info(String.format("after used %d ms", ((System.currentTimeMillis() - startTime))));
        return obj;
    }
}
