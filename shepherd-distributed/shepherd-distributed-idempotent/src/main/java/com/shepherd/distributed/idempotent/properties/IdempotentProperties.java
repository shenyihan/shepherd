package com.shepherd.distributed.idempotent.properties;

import com.shepherd.distributed.idempotent.enums.StorageTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentProperties.java
 * @Description TODO
 * @createTime 2020年10月09日 11:03:00
 */
@Data
@ConfigurationProperties(prefix = "shepherd.idempotent")
public class IdempotentProperties {

    /**
     * 一级存储类型
     * @see StorageTypeEnum
     */
    private String firstLevelType = StorageTypeEnum.REDIS.name();

    /**
     * 二级存储类型
     * @see StorageTypeEnum
     */
    private String secondLevelType;
}
