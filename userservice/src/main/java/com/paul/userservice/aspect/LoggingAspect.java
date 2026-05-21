package com.paul.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.paul.userservice.service.*.*(..))")
    public void logBeforeServiceMethods(JoinPoint joinPoint) {
        log.info("Executing method: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(
            value = "execution(* com.paul.userservice.service.*.*(..))",
            returning = "result"
    )
    public void logAfterServiceMethods(JoinPoint joinPoint) {
        log.info("Method executed successfully: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(
            value = "execution(* com.paul.userservice.service.*.*(..))",
            throwing = "exception"
    )
    public void logAfterException(JoinPoint joinPoint, Exception exception) {
        log.error("Exception in method: {} | Message: {}",
                joinPoint.getSignature().getName(),
                exception.getMessage()
        );
    }
}