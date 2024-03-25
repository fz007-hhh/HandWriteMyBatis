package com.fzbatis.start.session;

import com.fzbatis.start.builder.xml.XMLConfigBuilder;
import com.fzbatis.start.session.defaults.DefaultSqlSessionFactory;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.Reader;

// mybatis的入口
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) throws DocumentException, IOException, ClassNotFoundException {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
