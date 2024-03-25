package com.fzbatis.start;

import com.fzbatis.start.dao.IUserDao;
import com.fzbatis.start.io.Resources;
import com.fzbatis.start.session.SqlSession;
import com.fzbatis.start.session.SqlSessionFactory;
import com.fzbatis.start.session.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
//import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class StartApplicationTests {

    @Test
    public void testA() throws IOException, DocumentException, ClassNotFoundException {
        Reader reader = Resources.getResourceAsReader("mybatis_config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao iUserDao = sqlSession.getMapper(IUserDao.class);

        String res = iUserDao.queryUserInfoById("10001");
        System.out.println(res);
    }

}
