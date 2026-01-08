package com.demo.todolist.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String key = DynamicDataSourceContext.getCurrentKey();
        return key == null ? DynamicDataSourceContext.DEFAULT_KEY : key;
    }
}
