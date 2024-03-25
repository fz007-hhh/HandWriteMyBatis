package com.fzbatis.start.mapping;

import com.fzbatis.start.session.Configuration;

import java.util.Map;

// 映射语句类
// sql信息记录对象,记录包括：SQL类型，SQL语句，入参类型，出参类型等
public class MappedStatement {
//    private Configuration configuration;
    private String id;
    //select,update,delete
    private SqlCommandType sqlCommandType;
    //参数的数据类型
    private String parameterType;
    //返回值的数据类型
    private String resultType;
    //sql语句内容
    private String sql;
    //按顺序标记sql语句中的参数
    private Map<Integer, String> parameter;

//    public Configuration getConfiguration() {
//        return configuration;
//    }
//
//    public void setConfiguration(Configuration configuration) {
//        this.configuration = configuration;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<Integer, String> parameter) {
        this.parameter = parameter;
    }

    //public MappedStatement(){}

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(String id, SqlCommandType sqlCommandType,
                       String parameterType, String resultType, String sql, Map<Integer, String> parameter) {
//            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;

            mappedStatement.parameterType = parameterType;
            mappedStatement.resultType = resultType;
            mappedStatement.sql = sql;
            mappedStatement.parameter = parameter;
        }

        public MappedStatement build() {
//            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }


    }

    // 省略set.get
}
