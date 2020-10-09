package com.shepherd.springcloud.autoconfig;

import com.shepherd.distributed.idempotent.aspect.IdempotentAspect;
import com.shepherd.distributed.idempotent.db.impl.IdempotentStorageMysql;
import com.shepherd.distributed.idempotent.db.impl.IdempotentStorageRedis;
import com.shepherd.distributed.idempotent.factory.IdempotentStorageFactory;
import com.shepherd.distributed.idempotent.properties.IdempotentProperties;
import com.shepherd.distributed.idempotent.service.IdempotentService;
import com.shepherd.distributed.idempotent.service.impl.IdempotentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentAutoConfiguration.java
 * @Description TODO
 * @createTime 2020年10月09日 14:33:00
 */
@Slf4j
@Configuration
@ImportAutoConfiguration(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

    @Bean
    public IdempotentService idempotentService(){
        return new IdempotentServiceImpl();
    }

    @Bean
    public IdempotentAspect idempotentAspect(IdempotentService idempotentService){
        return new IdempotentAspect(idempotentService);
    }

    @Bean
    public IdempotentStorageFactory idempotentStorageFactory(){
        return new IdempotentStorageFactory();
    }

    @Bean
    public IdempotentStorageRedis idempotentStorageRedis() {
        return new IdempotentStorageRedis();
    }

    @ConditionalOnClass(JdbcTemplate.class)
    @Configuration
    protected static class JdbcTemplateConfiguration {
        @Bean
        public IdempotentStorageMysql idempotentStorageMysql() {
            return new IdempotentStorageMysql();
        }
    }
}
