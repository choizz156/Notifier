package io.github.choizz.notifier.logging;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("local")
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(io.github.choizz.notifier..*) && !within(io.github.choizz.notifier..*Test) && !within(io.github.choizz.notifier..*Config) && !within(io.github.choizz.notifier..filter..*)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        log.info("[실행 시작] 메서드: {}.{}(), 인자: {}", className, methodName, arguments);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("[실행 완료] 메서드: {}.{}(), 소요 시간: {}ms", className, methodName, executionTime);
            return result;
        } catch (Throwable throwable) {
            if (throwable instanceof NoSuchElementException || throwable instanceof IllegalArgumentException) {
                log.warn("[실행 예외(비즈니스)] 메서드: {}.{}(), 메시지: {}", className, methodName, throwable.getMessage());
            } else {
                log.error("[실행 예외 발생] 메서드: {}.{}(), 메시지: {}", className, methodName, throwable.getMessage());
            }
            throw throwable;
        }
    }
}
