package com.paul.deviceservice.aspect;

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

    @Before("execution(* com.paul.deviceservice.service..*(..))")
    public void logBeforeServiceMethods(JoinPoint joinPoint) {
        log.info("Executing service method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(
            value = "execution(* com.paul.deviceservice.service..*(..))",
            returning = "result"
    )
    public void logAfterServiceMethods(JoinPoint joinPoint, Object result) {
        log.info("Service method executed successfully: {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(
            value = "execution(* com.paul.deviceservice.service..*(..))",
            throwing = "exception"
    )
    public void logAfterException(JoinPoint joinPoint, Exception exception) {
        log.error("Exception in service method: {} | Message: {}",
                joinPoint.getSignature().toShortString(),
                exception.getMessage()
        );
    }
}