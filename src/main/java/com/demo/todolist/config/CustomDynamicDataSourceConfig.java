package com.demo.todolist.config;

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
@EnableConfigurationProperties(CustomDynamicDataSourceProperties.class)
public class CustomDynamicDataSourceConfig {

    @Bean
    public DataSource defaultDataSource(DataSourceProperties properties) {
        if (StringUtils.hasText(properties.getUrl())) {
            return properties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
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

    @Bean(name = "dataSource")
    @Primary
    public CustomDynamicRoutingDataSource dynamicRoutingDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource) {
        CustomDynamicRoutingDataSource routingDataSource = new CustomDynamicRoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put(CustomDynamicDataSourceContext.DEFAULT_KEY, defaultDataSource);
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}
