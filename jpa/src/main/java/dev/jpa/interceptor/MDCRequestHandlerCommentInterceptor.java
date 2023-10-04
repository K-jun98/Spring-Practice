package dev.jpa.interceptor;

import dev.jpa.log.LogContext;
import dev.jpa.log.LogContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MDCRequestHandlerCommentInterceptor implements StatementInspector {

//    private final LogContextHolder logContextHolder;

    @Override
    public String inspect(String sql) {
        MDC.put("query", sql);
//        LogContext logContext = logContextHolder.get();
//        logContext.setSql(sql);
        return sql;
    }

}
