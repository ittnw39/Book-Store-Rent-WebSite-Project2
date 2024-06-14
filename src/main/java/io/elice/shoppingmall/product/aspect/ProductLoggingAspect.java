package io.elice.shoppingmall.product.aspect;

import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ProductLoggingAspect {

    @Pointcut("execution(* io.elice.shoppingmall.product.service..*(..))")
    public void productPointcut() {
    }

    @Before("productPointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("메서드 호출: " + joinPoint.getSignature().toShortString() + ", 메서드 인자: " + Arrays.toString(joinPoint.getArgs()));
    }

    @Around("productPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        
        log.info(joinPoint.getSignature().toShortString() + " 메서드 실행시간: " + executionTime + "ms");
        return proceed;
    }

    @AfterReturning(pointcut = "productPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("메서드 리턴: " + joinPoint.getSignature().toShortString() + ", 반환값: " + result);
    }

    @AfterThrowing(pointcut = "productPointcut()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("예외 발생: " + joinPoint.getSignature().toShortString());
        log.error("에러 메시지: " + error.getMessage());
    }
}