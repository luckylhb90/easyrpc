package com.luckylhb.easyrpc.common.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供方发布服务的注解
 *
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component//加上spring 注解 ， 该注解也可被spring 扫描
public @interface ServiceExporter {

    // 服务发现用的唯一标识， 用于服务自动寻址
    String value() default "";

    Class<?> targetInterface(); // 防止实现类实现了多个接口

    String debugAddress() default "";
}
