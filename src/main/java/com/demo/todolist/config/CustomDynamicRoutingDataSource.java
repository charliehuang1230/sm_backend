package com.demo.todolist.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class CustomDynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String key = CustomDynamicDataSourceContext.getCurrentKey();
        return key == null ? CustomDynamicDataSourceContext.DEFAULT_KEY : key;
    }
}
