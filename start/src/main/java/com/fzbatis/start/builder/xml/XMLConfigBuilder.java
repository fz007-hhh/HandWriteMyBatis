package com.fzbatis.start.builder.xml;

import cn.hutool.core.io.resource.Resource;
import com.fzbatis.start.builder.BaseBuilder;
import com.fzbatis.start.io.Resources;
import com.fzbatis.start.mapping.MappedStatement;
import com.fzbatis.start.mapping.SqlCommandType;
import com.fzbatis.start.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// xml配置构建器，建造者模式
// 对xml中的配置进行解析，解析mapper标签和select标签
public class XMLConfigBuilder extends BaseBuilder {
    private Element root;

    public XMLConfigBuilder(Reader reader) throws DocumentException {
        super(new Configuration());
        //dom4j处理xml
        SAXReader saxReader=new SAXReader();
        Document document=saxReader.read(new InputSource(reader));
        //解析xml文件
        root= document.getRootElement();
    }

    //
    public Configuration parse() throws DocumentException, IOException, ClassNotFoundException {
//        读取<mappers>标签
        mapperElement(root.element("mappers"));
        return configuration;
    }

    private void mapperElement(Element mappers) throws DocumentException, IOException, ClassNotFoundException {
        List<Element> mapperList=mappers.elements("mapper");
        for(Element e:mapperList){
            String resource=e.attributeValue("resource");
            Reader reader = Resources.getResourceAsReader(resource);
            SAXReader saxReader = new SAXReader();
            //使用SaxReader读取xml文件
            Document document=saxReader.read(new InputSource(reader));
            //获取xml的根节点
            Element root=document.getRootElement();
            String namespace=root.attributeValue("namespace");

            //获取select标签内容
            List<Element> selectNodes=root.elements("select");
            for(Element node:selectNodes){
                // 解析select标签的属性 id、parameterType/resultType等等
                String id = node.attributeValue("id");
                String parameterType = node.attributeValue("parameterType");
                String resultType=node.attributeValue("resultType");
                // 解析select标签的内容，也就是sql语句
                String sql = node.getText();

                // ?匹配参数
                Map<Integer, String> parameter = new HashMap<>();
                // 正则表达式
                Pattern pattern=Pattern.compile("(#\\{(.*?)})");
                // 匹配正则表达式
                Matcher matcher=pattern.matcher(sql);
                for(int i=1;matcher.find();i++){
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    parameter.put(i,g2);
                    sql=sql.replace(g1,"?");
                }
                String msId=namespace+"."+id;
                String nodeName=node.getName();

                SqlCommandType sqlCommandType=SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
                //使用内部类构造出mapper-sql对象
                MappedStatement mappedStatement=new MappedStatement.Builder(msId,sqlCommandType,
                        parameterType,resultType,sql,parameter).build();
                //添加解析之后的sql
                configuration.addMappedStatement(mappedStatement);
            }

            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
