package com.paul.deviceservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around(
            "execution(* com.paul.deviceservice.controller..*(..)) || " +
                    "execution(* com.paul.deviceservice.service..*(..))"
    )
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.info("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(),
                    executionTime
            );
        }
    }
}