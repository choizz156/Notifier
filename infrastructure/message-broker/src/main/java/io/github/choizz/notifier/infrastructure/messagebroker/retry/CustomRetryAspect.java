// package io.github.choizz.notifier.infrastructure.messagebroker.retry;
//
// import java.lang.reflect.Method;
//
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.reflect.MethodSignature;
// import org.springframework.stereotype.Component;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Aspect
// @Component
// public class CustomRetryAspect {
//
// 	@Around("@annotation(io.github.choizz.notifier.infrastructure.messagebroker.retry.CustomRetry)")
// 	public Object handleRetry(ProceedingJoinPoint joinPoint) throws Throwable {
//
// 		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
// 		Method method = signature.getMethod();
// 		CustomRetry customRetry = method.getAnnotation(CustomRetry.class);
//
// 		int maxAttempts = customRetry.maxAttempts();
// 		long delay = customRetry.delay();
//
//
//
// 	}
// }
