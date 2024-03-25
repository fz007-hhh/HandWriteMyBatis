package com.fzbatis.start.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    /**
     * 读取文件，返回字符流
     * @param resource
     * @return
     * @throws IOException
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    /**
     * 获取当前线程使用的类加载器和文件名读取文件，返回值为字节流
     * 如果找不到文件就抛出异常
     * @param resource
     * @return
     * @throws IOException
     */
    private static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader[] classLoaders=getclassLoader();
        for(ClassLoader classLoader:classLoaders){
            InputStream inputStream=classLoader.getResourceAsStream(resource);
            if(inputStream!=null){
                return inputStream;
            }
        }
        throw new IOException("Could not find resource :"+resource);
    }

    //获取当前现成的加载器
    private static ClassLoader[] getclassLoader(){
        return new ClassLoader[]{
                ClassLoader.getSystemClassLoader(),Thread.currentThread().getContextClassLoader()
        };
    }

    // 根据类名生成Class对象
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
