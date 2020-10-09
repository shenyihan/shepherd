package com.shepherd.distributed.idempotent.request;

import com.shepherd.distributed.idempotent.enums.ReadWriteTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentRequest.java
 * @Description TODO
 * @createTime 2020年10月09日 10:39:00
 */
@Data
@Builder
public class IdempotentRequest {
    /**
     * 幂等Key
     */
    private String key;

    /**
     * 一级存储过期时间
     */
    private int firstLevelExpireTime;

    /**
     * 二级存储过期时间
     */
    private int secondLevelExpireTime;

    /**
     * 锁的过期时间
     */
    private int lockExpireTime;

    /**
     * 存储时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 读写类型
     */
    private ReadWriteTypeEnum readWriteType;
}
