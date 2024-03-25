# 手写MyBatis——XML解析封装成mapperStatement

这个项目是根据博客[手敲Mybatis(二)-Mapper的XML解析自动注册使用_mapper xml-CSDN博客](https://blog.csdn.net/dfBeautifulLive/article/details/124969342)敲的，主要目的是学习MyBatis的代理模式和内部架构。

两年前打国赛时接触过一点反射机制，当时是利用Zeebe+SpringBoot做一款服务编排工具（虽然做好之后发现还是使用Flowable更好...Zeebe的服务流每个节点都是分布式的，做事务控制不能用seata框架...），我记得当时是利用自定义注解+反射机制实现了方法参数校验，返回值封装的功能。简单来说，就是先自定义几个注解，标注在方法上面

```java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAspectJAutoProxy(exposeProxy = true)
public @interface SetVarible {
//    方法的各个参数名
    String[] params() default {};
//    方法所在的类
    Class< ? > methodUserClass() default Object.class;
//    方法名
    String methodName() default ("");
}

//.........
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EnableAspectJAutoProxy(exposeProxy = true)
public @interface MethodVarible {
    SetVarible[] variables() default {};
}

//........
@MethodVarible(variables = {
    @SetVarible(params = {"m1", "m2"}, methodName = "getNum",  methodUserClass = MyService.class),
    @SetVarible(params = {"m3", "0"}, methodName = "print", methodUserClass = MyService.class)
})
public void service01(MyService service){
    /*
     * 如果有需要的话，只需在这里对变量进行自定义处理即可
     * 处理之后，注解会自动调用方法*/
}
```

然后再写一个切面类，使用@Before、@After、@Around等注解就可以添加自己想要的效果

```java
@Aspect
@Component
@EnableAspectJAutoProxy(exposeProxy = true)
public class MyAspect {

//    自定义注解要执行的行为
    @After("@annotation(com.xx.xx.anotation.MethodVarible)")
    public void startMethod(JoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Object[] args = joinPoint.getArgs();
        MyService service= (MyService) args[0];
        //获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //拿到方法的注解，包括注解中的各种属性值
        MethodVarible methodVarible=methodSignature.getMethod().getAnnotation(MethodVarible.class);
        SetVarible[] setVaribles=methodVarible.variables();
        //获取变量map
        Map<String, Object> map=service.getMyMap();
        List<Object>results=new ArrayList<>();

        //注解中可能存在多个方法，用for循环逐个调用即可
        for (SetVarible setVarible : setVaribles) {
            List<String> params= Arrays.asList(setVarible.params());
            Class<?> clazz=setVarible.methodUserClass();
            //获取当前类下所有的方法
            Method[] methods=clazz.getDeclaredMethods();
            //从所有方法中找到指定名称的方法
            Method choosen_method = null;
            for (Method method : methods){
                if(method.getName().equals(setVarible.methodName())){
                    choosen_method = method;
                    break;
                }
            }
            List<Object> list=new ArrayList<>();
            //记录注解中各个方法的返回值，以备方法的嵌套调用，void的返回值是null，null也算是一个Object
            results.add(choosen_method.invoke(clazz.getDeclaredConstructor().newInstance(),list.toArray()));
        }

        System.out.println(results.get(0));
    }
}

```

不过当时还发现Zeebe和MyBatis都采用了xxFactory+xxFactoryBuild的大致模式，往往是下面这种构造过程：

```java
@Test
public void test(){
    SqlSessionFactory factory=SqlSessionFactoryBuilder.newFactory();
    SqlSession session=factory.openSession();
}
```

这么写可读性确实是很强的，但是我一直不太明白这么设计的意义是什么，当时也一直不知道什么叫代理模式，只是感觉如果要想在某方法上追加功能，完全可以通过注解的方式实现。这次看到了这位博主的博客，还是决定结合这篇博客动手探索探索这个问题。

## （1）mapper.xml的解析过程

这里使用SaxReader解析xml文件，然后依次提取出标签中的属性信息还有标签内容

![image-20240325161817905](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240325161817905.png)

只是提取出来还不行，还需要将这些字段封装到JavaBean类MappedStatement中去

![image-20240325163333851](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240325163333851.png)

## （2）代理对象

![image-20240323184943654](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240323184943654.png)

![image-20240323185133605](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240323185133605.png)

![image-20240325164906266](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240325164906266.png)

==每个Mapper接口类都对应着一个MapperProxyFactory，而MapperProxyFactory调用newInstance都可以生成一个代理对象==

关键在于这句方法的功能：

```java
public T newInstance(){
    return Proxy.newProxyInstance(classLoader,class,代理执行类);
}
```

这个代理执行类必须实现InvovationHandler接口，这是一个特殊接口，实现之后可以实现如下效果：

```java
class AClass{
  public int methodA();
  public int methodB();
  public int methodC();
};

class ProxyClass implements InvocationHandler{
    @Override
    public Objext invoke(Object proxy,Method method,Object[] args){
       //...
    }
};
public static void main(){
    //生成一个AClass的代理对象
    AClass obj=Proxy.newProxyInstance(AClass.getClassLoader,new Class[]{Aclass.class},new ProxyClass());
    // 执行时methodA会发现，会先进入invoke方法中
    // 这个因为现在这个obj已经是代理对象了，实际办事的事那个ProxyClass()
    obj.methodA();
    obj.methodB();
    obj.methodC();
}
```

#### Q1：这么做有什么意义？

其实可以发现，这么做的效果非常像@Around注解的使用，在某个方法上标注上@Around，就可以自定义方法的执行前后过程，实现参数校验，结果校验等效果。当然，你可能会说"我直接把校验过程写在方法中不行吗？"，当然可以，但是如果遇到频繁的修改，每次修改校验过程都需要动用那块代码，但是使用这种方法之后就可以先封装业务执行部分的代码，在通过编写切口方法修改校验规则。

使用代理对象就相当于是批量的@Around，我们可以在代理类中对方法进行重构。

## （3）封装成Configuration

![image-20240325164807747](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240325164807747.png)

## （4）封装DefaultSqlSessionFactory

![image-20240325165520500](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240325165520500.png)

这里类似c++中的单例模式，factory可以用来开启sql会话，不过configuration用了final修饰，这就意味着不管开启多少个会话，他们使用的configuration始终都是相同的。

![image-20240325165759211](https://typora-aliyun01.oss-cn-hangzhou.aliyuncs.com/img/image-20240325165759211.png)

这里的selectOne()的参数中，statement其实是 类似"com.big.zz.User" 的类名称，作用是作为key从Configuration的map缓存中获取sql对象
