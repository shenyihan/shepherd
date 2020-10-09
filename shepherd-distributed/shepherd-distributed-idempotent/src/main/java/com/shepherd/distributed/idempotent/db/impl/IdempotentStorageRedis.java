package com.shepherd.distributed.idempotent.db.impl;

import com.shepherd.distributed.idempotent.db.IdempotentStorage;
import com.shepherd.distributed.idempotent.enums.StorageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentStorageRedis.java
 * @Description TODO
 * @createTime 2020年10月02日 16:48:00
 */
@Slf4j
public class IdempotentStorageRedis implements IdempotentStorage {

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public StorageTypeEnum type() {
        return StorageTypeEnum.REDIS;
    }

    @Override
    public void setValue(String key, String value, long expireTime, TimeUnit timeUnit) {
        log.info("Redis Set key:{}, Value:{}, expireTime:{}, timeUnit:{}", key, value, expireTime, timeUnit);
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (bucket != null) {
            bucket.set(value, expireTime, timeUnit);
        }
    }

    @Override
    public String getValue(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        String value = bucket.get();
        log.info("Redis Get key:{}, Value:{}", key, value);
        return value;
    }
}
