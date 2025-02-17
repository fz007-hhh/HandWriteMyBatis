package com.fzbatis.start.binding;

import cn.hutool.core.lang.ClassScanner;
import com.fzbatis.start.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapperRegistry {

    /**
     * 存储代理器工厂容器
     * 每个Mapper类都对应着一个Mapper代理工厂，用来产生代理对象
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    // 获取容器中的对象并生成代理对象
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            // 生成代理对象并返回
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public <T> void addMapper(Class<T> type) {
        /* Mapper 必须是接口才会注册 */
        if (type.isInterface()) {
            if (hasMapper(type)) {
                // 如果重复添加了，报错
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");
            }
            // 注册映射器代理工厂
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public void addMappers(String packageName) {
        // 生成class对象，包下有几个类对象生成几个类对象
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }

}
