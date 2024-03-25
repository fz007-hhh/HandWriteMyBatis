package com.fzbatis.start.binding;

import com.fzbatis.start.mapping.MappedStatement;
import com.fzbatis.start.mapping.SqlCommandType;
import com.fzbatis.start.session.Configuration;
import com.fzbatis.start.session.SqlSession;

import java.lang.reflect.Method;

public class MapperMethod {
    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        command = new SqlCommand(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (command.getType()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case UPDATE:
                break;
            case SELECT:
                result = sqlSession.selectOne(command.getName(), args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + command.getName());
        }
        return result;
    }


    public static class SqlCommand {
        private final String name;
        private final SqlCommandType type;


        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementName = mapperInterface.getName() + "." + method.getName();
            MappedStatement ms = configuration.getMappedStatement(statementName);
            this.name = ms.getId();
            this.type = ms.getSqlCommandType();

        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }

}
