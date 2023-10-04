package dev.jpa.interceptor;

import dev.jpa.log.LogContext;
import dev.jpa.log.LogContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LogContextInterceptor implements HandlerInterceptor {

   /* private final LogContextHolder logContextHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LogContext logContext = new LogContext();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String handlerName = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            String controllerInfo = handlerName + "." + methodName;
            logContext.setHandler(controllerInfo);
        }
        logContextHolder.setLogContext(logContext);
        return true;
    }*/
}
