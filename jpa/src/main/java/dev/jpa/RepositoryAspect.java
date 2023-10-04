package dev.jpa;

import dev.jpa.log.LogContext;
import dev.jpa.log.LogContextHolder;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryAspect {

//    private final LogContextHolder logContextHolder;

    @Around("execution(* dev.jpa.repository.PostRepository.*(..))")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//        LogContext logContext = logContextHolder.get();

        long start = System.currentTimeMillis();
        Object returnValue = joinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println("아이디" + MDC.get("id") + " 호출 컨트롤러 " + MDC.get("handler") + " 걸린 시간 " + (end - start) + " ms" + " 실행된 쿼리 = " + MDC.get("query"));
//        System.out.println("로그 컨텍스트 아이디" + logContext.getId());
        return returnValue;
    }

}
