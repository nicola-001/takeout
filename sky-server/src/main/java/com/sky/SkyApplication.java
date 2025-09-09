package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
public class SkyApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SkyApplication.class, args);

        System.out.println("容器创建成功run：" + run);
    }
}
/*
 * 1.全局异常
 * 2.拦截器
 *      将用户信息存储在ThreadLocal中
 *      扩展Spring MVC框架消息转换器
 * 3.登录时 token生成
 * 4.swagger
 * 5.自定义注解(自定义填充公共字段) ：@ControllerAdvice + @ExceptionHandler
 *  用到的技术点： AOP 反射 注解 切面
 * 注：
 *  @JsonIgnore：查询出来：但是不向前端展示
 * @TableField：不查询该字段
 * */

/*
    开发技巧：
        1.dto表示前端传递过来的数据
          vo表示后端响应给前端的数据
          entity表示与数据库直接映射的实体类
        2.写sql语句时，如果写查询d表中的全部数据可以直接写d.*  但是VO数据字段必须和数据库中的表一致 否则映射不上
        3.可以写 LIKE concat('%',#{name},'%')用于模糊查询
* */
