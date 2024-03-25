package com.fzbatis.start.session;

import com.fzbatis.start.binding.MapperRegistry;
import com.fzbatis.start.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认配置类
 */
public class Configuration {

    // 映射注册机
    protected MapperRegistry mapperRegistry = new MapperRegistry();

    // 将<select/>标签的namespace+id和封装好的sql对象绑定起来
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }
}
