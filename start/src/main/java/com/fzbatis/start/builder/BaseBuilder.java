package com.fzbatis.start.builder;


import com.fzbatis.start.session.Configuration;

public class BaseBuilder {
    // 配置解析类
    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
