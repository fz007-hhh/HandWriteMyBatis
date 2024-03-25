package com.fzbatis.start.session.defaults;

import com.fzbatis.start.mapping.MappedStatement;
import com.fzbatis.start.session.Configuration;
import com.fzbatis.start.session.SqlSession;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    //private MapperRegistry mapperRegistry;
    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("你被代理了！" + "\n方法：" + statement + "\n入参：" + parameter + "\n待执行SQL：" + mappedStatement.getSql());
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
