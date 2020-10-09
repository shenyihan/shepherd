package com.shepherd.distributed.idempotent.db.impl;

import com.shepherd.distributed.idempotent.db.IdempotentStorage;
import com.shepherd.distributed.idempotent.enums.StorageTypeEnum;

import java.util.concurrent.TimeUnit;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentStorageMysql.java
 * @Description TODO
 * @createTime 2020年10月02日 16:49:00
 */
public class IdempotentStorageMysql implements IdempotentStorage {


    @Override
    public StorageTypeEnum type() {
        return StorageTypeEnum.MYSQL;
    }

    @Override
    public void setValue(String key, String value, long expireTime, TimeUnit timeUnit) {

    }

    @Override
    public String getValue(String key) {
        return null;
    }
}
