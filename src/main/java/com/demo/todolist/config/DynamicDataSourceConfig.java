package com.demo.todolist.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class DynamicDataSourceConfig {

    @Value("${dynamic.datasource.ttl-minutes:30}")
    private long ttlMinutes;

    public Duration getTtl() {
        return Duration.ofMinutes(ttlMinutes);
    }

    @Bean
    public DataSource defaultDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "dataSource")
    @Primary
    public DynamicRoutingDataSource dynamicRoutingDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put(DynamicDataSourceContext.DEFAULT_KEY, defaultDataSource);
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}
