package com.fzbatis.start.session.defaults;

import com.fzbatis.start.session.Configuration;
import com.fzbatis.start.session.SqlSession;
import com.fzbatis.start.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        // 使用唯一的configuration
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        //开启一个默认的sql会话
        return new DefaultSqlSession(configuration);
    }
}
