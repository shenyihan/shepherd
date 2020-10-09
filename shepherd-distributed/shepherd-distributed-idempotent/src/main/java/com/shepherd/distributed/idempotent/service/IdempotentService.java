package com.shepherd.distributed.idempotent.service;

import com.shepherd.distributed.idempotent.enums.ReadWriteTypeEnum;
import com.shepherd.distributed.idempotent.request.IdempotentRequest;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentService.java
 * @Description TODO
 * @createTime 2020年10月02日 17:19:00
 */
public interface IdempotentService {

    /**
     * 幂等执行
     * @param key 幂等Key
     * @param lockExpireTime 锁的过期时间
     * @param firstLevelExpireTime 一级存储过期时间
     * @param secondLevelExpireTime 二级存储过期时间
     * @param timeUnit 存储时间单位
     * @param readWriteType 读写类型
     * @param execute 要执行的逻辑
     * @param fail Key已经存在，幂等拦截后的执行逻辑
     * @return
     */
    <T> T execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, ReadWriteTypeEnum readWriteType, Supplier<T> execute, Supplier<T> fail);

    /**
     * 幂等执行
     * @param key 幂等Key
     * @param lockExpireTime 锁的过期时间
     * @param firstLevelExpireTime 一级存储过期时间
     * @param secondLevelExpireTime 二级存储过期时间
     * @param timeUnit 存储时间单位
     * @param execute 要执行的逻辑
     * @param fail Key已经存在，幂等拦截后的执行逻辑
     * @return
     */
    <T> T execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Supplier<T> execute, Supplier<T> fail);

    /**
     * 幂等执行
     * @param request 幂等参数
     * @param execute 要执行的逻辑
     * @param fail Key已经存在，幂等拦截后的执行逻辑
     * @return
     */
    <T> T execute(IdempotentRequest request, Supplier<T> execute, Supplier<T> fail);

    /**
     * 幂等执行
     * @param key 幂等Key
     * @param lockExpireTime 锁的过期时间
     * @param firstLevelExpireTime 一级存储过期时间
     * @param secondLevelExpireTime 二级存储过期时间
     * @param timeUnit 存储时间单位
     * @param execute 要执行的逻辑
     * @param fail Key已经存在，幂等拦截后的执行逻辑
     * @return
     */
    void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, ReadWriteTypeEnum readWriteType, Runnable execute, Runnable fail);

    /**
     * 幂等执行
     * @param key 幂等Key
     * @param lockExpireTime 锁的过期时间
     * @param firstLevelExpireTime 一级存储过期时间
     * @param secondLevelExpireTime 二级存储过期时间
     * @param timeUnit 存储时间单位
     * @param execute 要执行的逻辑
     * @param fail Key已经存在，幂等拦截后的执行逻辑
     * @return
     */
    void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Runnable execute, Runnable fail);

    /**
     * 幂等执行
     * @param request 幂等参数
     * @param execute 要执行的逻辑
     * @param fail Key已经存在，幂等拦截后的执行逻辑
     * @return
     */
    void execute(IdempotentRequest request, Runnable execute, Runnable fail);
}
