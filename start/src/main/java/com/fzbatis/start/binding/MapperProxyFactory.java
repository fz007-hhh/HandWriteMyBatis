package com.fzbatis.start.binding;

import com.fzbatis.start.session.SqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    // 原对象类
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    // 第一个参数：用哪个类加载器去加载代理对象
    // 第二个参数：动态代理类需要实现的接口
    // 第三个参数：动态代理方法在执行时，会调用第三个参数里面的invoke方法去执行
    // 方法返回的对象
    // Proxy.newProxyInstance代理的是接口
    @SuppressWarnings("unchecked")
    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

}
