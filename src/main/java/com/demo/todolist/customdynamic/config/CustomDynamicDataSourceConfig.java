package com.demo.todolist.customdynamic.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({
        CustomDynamicDataSourceProperties.class,
        CustomDynamicDataSourceSettings.class
})
public class CustomDynamicDataSourceConfig {

    @Bean
    public DataSource defaultDataSource(DataSourceProperties properties,
                                        CustomDynamicDataSourceSettings dynamicSettings,
                                        @Qualifier("dynamicDataSources") Map<String, DataSource> dynamicDataSources) {
        if (StringUtils.hasText(properties.getUrl())) {
            return properties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
        }

        if (dynamicSettings.hasPrimaryDataSource()) {
            DataSource primary = dynamicDataSources.get(dynamicSettings.getPrimary());
            if (primary == null) {
                throw new IllegalStateException("Primary datasource '" + dynamicSettings.getPrimary()
                        + "' is missing url/username configuration");
            }
            return primary;
        }

        // No-op datasource to avoid connecting during startup. Any accidental use will fail fast.
        return new AbstractDataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("Default datasource not configured");
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }
        };
    }

    @Bean(name = "dynamicDataSources")
    public Map<String, DataSource> dynamicDataSources(CustomDynamicDataSourceSettings settings) {
        Map<String, DataSource> dataSources = new HashMap<>();
        for (Map.Entry<String, CustomDynamicDataSourceSettings.DataSourceConfig> entry : settings.getDatasource().entrySet()) {
            String name = entry.getKey();
            CustomDynamicDataSourceSettings.DataSourceConfig config = entry.getValue();
            if (!StringUtils.hasText(config.getUrl())) {
                continue;
            }
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(config.getUrl());
            dataSource.setUsername(config.getUsername());
            dataSource.setPassword(config.getPassword());
            if (StringUtils.hasText(config.getDriverClassName())) {
                dataSource.setDriverClassName(config.getDriverClassName());
            }
            if (config.getMaximumPoolSize() != null) {
                dataSource.setMaximumPoolSize(config.getMaximumPoolSize());
            }
            if (config.getMinimumIdle() != null) {
                dataSource.setMinimumIdle(config.getMinimumIdle());
            }
            if (config.getConnectionTimeout() != null) {
                dataSource.setConnectionTimeout(config.getConnectionTimeout());
            }
            if (config.getIdleTimeout() != null) {
                dataSource.setIdleTimeout(config.getIdleTimeout());
            }
            dataSource.setPoolName("dynamic-config-" + name);
            dataSources.put(name, dataSource);
        }
        return dataSources;
    }

    @Bean(name = "dataSource")
    @Primary
    public CustomDynamicRoutingDataSource dynamicRoutingDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource,
                                                                   @Qualifier("dynamicDataSources") Map<String, DataSource> dynamicDataSources) {
        CustomDynamicRoutingDataSource routingDataSource = new CustomDynamicRoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put(CustomDynamicDataSourceContext.DEFAULT_KEY, defaultDataSource);
        targets.putAll(dynamicDataSources);
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}
