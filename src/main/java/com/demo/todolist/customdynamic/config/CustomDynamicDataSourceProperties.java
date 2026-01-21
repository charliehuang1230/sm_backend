package com.demo.todolist.customdynamic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "dynamic.datasource")
public class CustomDynamicDataSourceProperties {
    private long ttlMinutes = 30;
    private int maximumPoolSize = 10;
    private int minimumIdle = 1;
    private long connectionTimeoutMs = 5000;
    private long idleTimeoutMs = 300000;
    private Map<String, DatabaseConfig> databases = new HashMap<>();

    public long getTtlMinutes() {
        return ttlMinutes;
    }

    public void setTtlMinutes(long ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }

    public Map<String, DatabaseConfig> getDatabases() {
        return databases;
    }

    public void setDatabases(Map<String, DatabaseConfig> databases) {
        this.databases = databases;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(long connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public long getIdleTimeoutMs() {
        return idleTimeoutMs;
    }

    public void setIdleTimeoutMs(long idleTimeoutMs) {
        this.idleTimeoutMs = idleTimeoutMs;
    }

    public static class DatabaseConfig {
        private com.demo.todolist.customdynamic.dto.DbType dbType;
        private String host;
        private int port;
        private String database;
        private Boolean useServiceName;

        public com.demo.todolist.customdynamic.dto.DbType getDbType() {
            return dbType;
        }

        public void setDbType(com.demo.todolist.customdynamic.dto.DbType dbType) {
            this.dbType = dbType;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public Boolean getUseServiceName() {
            return useServiceName;
        }

        public void setUseServiceName(Boolean useServiceName) {
            this.useServiceName = useServiceName;
        }
    }
}
