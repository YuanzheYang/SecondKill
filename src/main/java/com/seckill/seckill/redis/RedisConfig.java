package com.seckill.seckill.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="spring.redis")
public class RedisConfig {
    private String host;
    private int port;
    private int timeout = 3;
    private int maxactive  = 100;
    private String password;
    private int maxidle = 1000;
    private int maxwait = 3;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setMaxactive(int maxactive) {
        this.maxactive = maxactive;
    }

    public void setMaxidle(int maxidle) {
        this.maxidle = maxidle;
    }

    public void setMaxwait(int maxwait) {
        this.maxwait = maxwait;
    }

    public int getMaxactive() {
        return maxactive;
    }

    public int getMaxidle() {
        return maxidle;
    }

    public int getMaxwait() {
        return maxwait;
    }
}
