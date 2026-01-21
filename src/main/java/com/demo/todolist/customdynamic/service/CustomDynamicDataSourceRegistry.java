package com.demo.todolist.customdynamic.service;

import com.demo.todolist.customdynamic.config.CustomDynamicDataSourceContext;
import com.demo.todolist.customdynamic.config.CustomDynamicDataSourceProperties;
import com.demo.todolist.customdynamic.config.CustomDynamicRoutingDataSource;
import com.demo.todolist.customdynamic.dto.DbType;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectRequest;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectResponse;
import com.demo.todolist.customdynamic.dto.CustomDynamicConnectionInfo;
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
public class CustomDynamicDataSourceRegistry {

    private final CustomDynamicRoutingDataSource routingDataSource;
    private final DataSource defaultDataSource;
    private final Duration ttl;
    private final CustomDynamicDataSourceProperties properties;
    private final Map<String, DataSourceHolder> dataSources = new ConcurrentHashMap<>();

    public CustomDynamicDataSourceRegistry(CustomDynamicRoutingDataSource routingDataSource,
                                     @Qualifier("defaultDataSource") DataSource defaultDataSource,
                                     CustomDynamicDataSourceProperties properties) {
        this.routingDataSource = routingDataSource;
        this.defaultDataSource = defaultDataSource;
        this.ttl = Duration.ofMinutes(properties.getTtlMinutes());
        this.properties = properties;
    }

    public CustomDynamicConnectResponse connect(CustomDynamicConnectRequest request) {
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
        dataSources.put(connectionId, new DataSourceHolder(dataSource, request.getDatabaseName(), now, now));
        refreshRoutingDataSources();

        return new CustomDynamicConnectResponse(connectionId, now.plus(ttl));
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

    public List<CustomDynamicConnectionInfo> getConnectionInfos() {
        List<CustomDynamicConnectionInfo> infos = new ArrayList<>();
        for (Map.Entry<String, DataSourceHolder> entry : dataSources.entrySet()) {
            infos.add(CustomDynamicConnectionInfo.builder()
                    .connectionId(entry.getKey())
                    .databaseName(entry.getValue().getDatabaseName())
                    .build());
        }
        infos.sort((left, right) -> left.getConnectionId().compareTo(right.getConnectionId()));
        return infos;
    }

    public int removeAll() {
        List<DataSourceHolder> holders = new ArrayList<>(dataSources.values());
        dataSources.clear();
        for (DataSourceHolder holder : holders) {
            holder.close();
        }
        refreshRoutingDataSources();
        return holders.size();
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
        targets.put(CustomDynamicDataSourceContext.DEFAULT_KEY, defaultDataSource);
        for (Map.Entry<String, DataSourceHolder> entry : dataSources.entrySet()) {
            targets.put(entry.getKey(), entry.getValue().getDataSource());
        }
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.afterPropertiesSet();
    }

    private HikariDataSource buildDataSource(String connectionId, CustomDynamicConnectRequest request) {
        CustomDynamicDataSourceProperties.DatabaseConfig databaseConfig = getDatabaseConfig(request);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildJdbcUrl(databaseConfig));
        config.setUsername(request.getUsername());
        config.setPassword(request.getPassword());
        config.setDriverClassName(driverClassName(databaseConfig.getDbType()));
        config.setPoolName("dynamic-" + connectionId);
        config.setMaximumPoolSize(properties.getMaximumPoolSize());
        config.setMinimumIdle(properties.getMinimumIdle());
        config.setConnectionTimeout(properties.getConnectionTimeoutMs());
        config.setIdleTimeout(properties.getIdleTimeoutMs());
        return new HikariDataSource(config);
    }

    private String buildJdbcUrl(CustomDynamicDataSourceProperties.DatabaseConfig databaseConfig) {
        return switch (databaseConfig.getDbType()) {
            case POSTGRES -> "jdbc:postgresql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort()
                    + "/" + databaseConfig.getDatabase();
            case ORACLE -> buildOracleJdbcUrl(databaseConfig);
            case MYSQL -> "jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort()
                    + "/" + databaseConfig.getDatabase();
            case MSSQL -> "jdbc:sqlserver://" + databaseConfig.getHost() + ":" + databaseConfig.getPort()
                    + ";databaseName=" + databaseConfig.getDatabase();
        };
    }

    private String buildOracleJdbcUrl(CustomDynamicDataSourceProperties.DatabaseConfig databaseConfig) {
        String hostPort = databaseConfig.getHost() + ":" + databaseConfig.getPort();
        if (Boolean.TRUE.equals(databaseConfig.getUseServiceName())) {
            return "jdbc:oracle:thin:@//" + hostPort + "/" + databaseConfig.getDatabase();
        }
        return "jdbc:oracle:thin:@" + hostPort + ":" + databaseConfig.getDatabase();
    }

    private String driverClassName(DbType dbType) {
        return switch (dbType) {
            case POSTGRES -> "org.postgresql.Driver";
            case ORACLE -> "oracle.jdbc.OracleDriver";
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case MSSQL -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        };
    }

    private CustomDynamicDataSourceProperties.DatabaseConfig getDatabaseConfig(CustomDynamicConnectRequest request) {
        String databaseName = request.getDatabaseName();
        CustomDynamicDataSourceProperties.DatabaseConfig config = properties.getDatabases().get(databaseName);
        if (config == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "unknown database name: " + databaseName);
        }
        return config;
    }

    private static class DataSourceHolder {
        private final DataSource dataSource;
        private final String databaseName;
        private final Instant createdAt;
        private volatile Instant lastAccess;

        DataSourceHolder(DataSource dataSource, String databaseName, Instant createdAt, Instant lastAccess) {
            this.dataSource = dataSource;
            this.databaseName = databaseName;
            this.createdAt = createdAt;
            this.lastAccess = lastAccess;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public String getDatabaseName() {
            return databaseName;
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
