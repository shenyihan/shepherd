package com.shepherd.distributed.idempotent.anotation;

import com.shepherd.distributed.idempotent.enums.ReadWriteTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName Idempotent.java
 * @Description TODO 幂等性注解
 * @createTime 2020年10月02日 15:51:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 锁的名称，唯一性（默认为方法名）
     * @return
     */
    String value() default "";

    /**
     * SPEL表达式，获取幂等Key，默认会从线程上下文中获取框架提供的幂等ID
     * @return
     */
    String spelKey() default "";

    /**
     * 一级存储过期时间
     * @return
     */
    int firstLevelExpireTime() default 10;

    /**
     * 二级存储过期时间
     * @return
     */
    int secondLevelExpireTime() default 60 * 60 * 24 * 365;

    /**
     * 读写类型
     * ORDER：顺序读写，先读一级缓存，没有再都二级缓存，先写一级缓存，再写二级缓存
     * PARALLEL：并行读写，同时读一级和二级缓存，取返回最快的并且有值的结果，同时写一级和二级缓存
     * @return
     */
    ReadWriteTypeEnum readWriteType() default ReadWriteTypeEnum.PARALLEL;

    /**
     * 锁的过期时间
     * @return
     */
    int lockExpireTime() default 10;

    /**
     * 存储时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 触发幂等限制时调用同类中的方法进行后续处理
     * @return
     */
    String idempotentHandler() default "";

    /**
     * 触发幂等限制时调用其他类中的方法进行后续处理
     * @return
     */
    Class<?>[] idempotentHandlerClass() default {};
}
