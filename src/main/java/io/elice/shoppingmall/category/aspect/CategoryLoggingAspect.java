package io.elice.shoppingmall.category.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CategoryLoggingAspect {

    @Pointcut("execution(* io.elice.shoppingmall.category.service..*(..))")
    public void categoryPointcut() {}

    @Before("categoryPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("[메서드 시작] 메서드 : {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "categoryPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (result != null) {
            log.info("[메서드 종료] 메서드 : {}, 반환값 : {}", joinPoint.getSignature().toShortString(), result);
        } else {
            log.info("[메서드 종료] 메서드 : {}", joinPoint.getSignature().toShortString());
        }
    }

    @AfterThrowing(value = "categoryPointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("[예외 발생] 메서드 : {}, 에러 메시지: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}
