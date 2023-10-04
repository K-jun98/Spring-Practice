package dev.jpa.log;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LogContext {

    private final UUID id;
    private final long startTimeMillis;
    private int methodDepth = 0;
    private String sql;
    private String handler;

    public LogContext() {
        this.id = UUID.randomUUID();
        this.startTimeMillis = System.currentTimeMillis();
    }

    public void increaseCall() {
        methodDepth++;
    }

    public void decreaseCall() {
        methodDepth--;
    }

    public String depthPrefix(String prefixString) {
        if (methodDepth == 1) {
            return "|" + prefixString;
        }
        String bar = "|" + " ".repeat(prefixString.length());
        return bar.repeat(methodDepth - 1) + "|" + prefixString;
    }

    public long totalTakenTime() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
