package com.shepherd.distributed.idempotent.db;

import com.shepherd.distributed.idempotent.enums.StorageTypeEnum;

import java.util.concurrent.TimeUnit;

/**
 * 缓存的实现
 */
public interface IdempotentStorage {

    StorageTypeEnum type();

    void setValue(String key, String value, long expireTime, TimeUnit timeUnit);

    String getValue(String key);
}
