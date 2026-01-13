package com.demo.todolist.service;

import com.demo.todolist.config.DynamicDataSourceContext;
import com.demo.todolist.config.DynamicDataSourceConfig;
import com.demo.todolist.config.DynamicRoutingDataSource;
import com.demo.todolist.dto.DbType;
import com.demo.todolist.dto.DynamicConnectRequest;
import com.demo.todolist.dto.DynamicConnectResponse;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamicDataSourceRegistry {

    private final DynamicRoutingDataSource routingDataSource;
    private final DataSource defaultDataSource;
    private final Duration ttl;
    private final Map<String, DataSourceHolder> dataSources = new ConcurrentHashMap<>();

    public DynamicDataSourceRegistry(DynamicRoutingDataSource routingDataSource,
                                     @Qualifier("defaultDataSource") DataSource defaultDataSource,
                                     DynamicDataSourceConfig config) {
        this.routingDataSource = routingDataSource;
        this.defaultDataSource = defaultDataSource;
        this.ttl = config.getTtl();
    }

    public DynamicConnectResponse connect(DynamicConnectRequest request) {
        String connectionId = UUID.randomUUID().toString();
        HikariDataSource dataSource;
        try {
            dataSource = buildDataSource(connectionId, request);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid datasource config: " + ex.getMessage(), ex);
        }

        try (Connection ignored = dataSource.getConnection()) {
            // validate connection
        } catch (Exception ex) {
            dataSource.close();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "database connection failed: " + ex.getMessage(), ex);
        }

        Instant now = Instant.now();
        dataSources.put(connectionId, new DataSourceHolder(dataSource, now, now));
        refreshRoutingDataSources();

        return new DynamicConnectResponse(connectionId, now.plus(ttl));
    }

    public void touch(String connectionId) {
        DataSourceHolder holder = dataSources.get(connectionId);
        if (holder == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "connectionId not found");
        }
        holder.setLastAccess(Instant.now());
    }

    public void ensureExists(String connectionId) {
        if (!dataSources.containsKey(connectionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "connectionId not found");
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanupExpired() {
        Instant now = Instant.now();
        List<String> expired = new ArrayList<>();
        for (Map.Entry<String, DataSourceHolder> entry : dataSources.entrySet()) {
            if (Duration.between(entry.getValue().getLastAccess(), now).compareTo(ttl) > 0) {
                expired.add(entry.getKey());
            }
        }
        for (String key : expired) {
            remove(key);
        }
    }

    public void remove(String connectionId) {
        DataSourceHolder holder = dataSources.remove(connectionId);
        if (holder != null) {
            holder.close();
            refreshRoutingDataSources();
        }
    }

    private void refreshRoutingDataSources() {
        Map<Object, Object> targets = new HashMap<>();
        targets.put(DynamicDataSourceContext.DEFAULT_KEY, defaultDataSource);
        for (Map.Entry<String, DataSourceHolder> entry : dataSources.entrySet()) {
            targets.put(entry.getKey(), entry.getValue().getDataSource());
        }
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.afterPropertiesSet();
    }

    private HikariDataSource buildDataSource(String connectionId, DynamicConnectRequest request) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildJdbcUrl(request.dbType(), request.host(), request.port(), request.database()));
        config.setUsername(request.username());
        config.setPassword(request.password());
        config.setDriverClassName(driverClassName(request.dbType()));
        config.setPoolName("dynamic-" + connectionId);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(300000);
        return new HikariDataSource(config);
    }

    private String buildJdbcUrl(DbType dbType, String host, int port, String database) {
        return switch (dbType) {
            case POSTGRES -> "jdbc:postgresql://" + host + ":" + port + "/" + database;
            case ORACLE -> "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
            case MYSQL -> "jdbc:mysql://" + host + ":" + port + "/" + database;
            case MSSQL -> "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database;
        };
    }

    private String driverClassName(DbType dbType) {
        return switch (dbType) {
            case POSTGRES -> "org.postgresql.Driver";
            case ORACLE -> "oracle.jdbc.OracleDriver";
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case MSSQL -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        };
    }

    private static class DataSourceHolder {
        private final DataSource dataSource;
        private final Instant createdAt;
        private volatile Instant lastAccess;

        DataSourceHolder(DataSource dataSource, Instant createdAt, Instant lastAccess) {
            this.dataSource = dataSource;
            this.createdAt = createdAt;
            this.lastAccess = lastAccess;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Instant getLastAccess() {
            return lastAccess;
        }

        public void setLastAccess(Instant lastAccess) {
            this.lastAccess = lastAccess;
        }

        public void close() {
            if (dataSource instanceof HikariDataSource hikari) {
                hikari.close();
            }
        }
    }
}
