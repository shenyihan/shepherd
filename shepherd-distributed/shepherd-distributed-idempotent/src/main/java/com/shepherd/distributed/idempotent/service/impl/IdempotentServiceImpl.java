package com.shepherd.distributed.idempotent.service.impl;

import com.shepherd.distributed.idempotent.db.IdempotentStorage;
import com.shepherd.distributed.idempotent.enums.ReadWriteTypeEnum;
import com.shepherd.distributed.idempotent.enums.StorageTypeEnum;
import com.shepherd.distributed.idempotent.factory.IdempotentStorageFactory;
import com.shepherd.distributed.idempotent.properties.IdempotentProperties;
import com.shepherd.distributed.idempotent.request.IdempotentRequest;
import com.shepherd.distributed.idempotent.service.IdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentServiceImpl.java
 * @Description TODO
 * @createTime 2020年10月02日 17:21:00
 */
@Slf4j
public class IdempotentServiceImpl implements IdempotentService {

    @Autowired
    IdempotentProperties idempotentProperties;

    @Autowired
    private IdempotentStorageFactory idempotentStorageFactory;

    /**
     * 幂等Key对应的默认值
     */
    private String idempotentDefaultValue = "1";

    @Override
    public <T> T execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, ReadWriteTypeEnum readWriteType, Supplier<T> execute, Supplier<T> fail) {
        IdempotentRequest idempotentRequest = IdempotentRequest.builder().key(key).firstLevelExpireTime(firstLevelExpireTime)
                .lockExpireTime(lockExpireTime).secondLevelExpireTime(secondLevelExpireTime)
                .readWriteType(readWriteType).timeUnit(timeUnit).build();
        return execute(idempotentRequest,execute,fail);
    }

    @Override
    public <T> T execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Supplier<T> execute, Supplier<T> fail) {
        return  execute(key, lockExpireTime, firstLevelExpireTime, secondLevelExpireTime, timeUnit, ReadWriteTypeEnum.PARALLEL, execute, fail);
    }

    @Override
    public <T> T execute(IdempotentRequest request, Supplier<T> execute, Supplier<T> fail) {
        IdempotentStorage idempotentStorage2 = null;
        if (StringUtils.hasText(idempotentProperties.getSecondLevelType())) {
            idempotentStorage2 = idempotentStorageFactory.getIdempotentStorage(StorageTypeEnum.valueOf(idempotentProperties.getSecondLevelType()));
        }
        IdempotentStorage idempotentStorage1 = idempotentStorageFactory.getIdempotentStorage(StorageTypeEnum.valueOf(idempotentProperties.getFirstLevelType()));

        if (request.getReadWriteType() == ReadWriteTypeEnum.ORDER) {
            return orderExecute(idempotentStorage1,idempotentStorage2,request,execute,fail);
        }
        if (request.getReadWriteType() == ReadWriteTypeEnum.PARALLEL) {
            return parallelExecute(idempotentStorage1,idempotentStorage2,request,execute,fail);
        }

        return fail.get();
    }

    private  <T> T parallelExecute(IdempotentStorage idempotentStorage1, IdempotentStorage idempotentStorage2, IdempotentRequest request, Supplier<T> execute, Supplier<T> fail) {
        List<String> results = getParallelExecuteResults(request.getKey(), idempotentStorage1, idempotentStorage2);
        if (results.stream().filter(StringUtils::hasText).count() == 0) {
            T executeResult = execute.get();
            parallelWriteResults(idempotentStorage1, idempotentStorage2, request);
            return executeResult;
        }

        return fail.get();
    }

    private List<String> getParallelExecuteResults(String key, IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage) {
        CompletableFuture<String> firstReadFuture = CompletableFuture.supplyAsync(() -> {
            return firstIdempotentStorage.getValue(key);
        });

        CompletableFuture<String> secondReadFuture = CompletableFuture.supplyAsync(() -> {
            if (secondIdempotentStorage != null) {
                return secondIdempotentStorage.getValue(key);
            }
            return null;
        });

        CompletableFuture<Void> readCombinedFuture = CompletableFuture.allOf(firstReadFuture, secondReadFuture);
        try {
            readCombinedFuture.get();
        } catch (Exception e) {
            log.error("并行读异常", e);
        }

        return Stream.of(firstReadFuture, secondReadFuture).map(CompletableFuture::join).collect(Collectors.toList());
    }

    private void parallelWriteResults(IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage, IdempotentRequest request) {
        CompletableFuture<String> firstWriteFuture = CompletableFuture.supplyAsync(() -> {
            firstIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getFirstLevelExpireTime(), request.getTimeUnit());
            return null;
        });
        CompletableFuture<String> secondWriteFuture = CompletableFuture.supplyAsync(() -> {
            if (StringUtils.hasText(idempotentProperties.getSecondLevelType())) {
                secondIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getSecondLevelExpireTime(), request.getTimeUnit());
            }
            return null;
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(firstWriteFuture, secondWriteFuture);
        try {
            combinedFuture.get();
        } catch (Exception e) {
            log.error("并行写异常", e);
        }
    }

    private <T> T orderExecute(IdempotentStorage idempotentStorage1, IdempotentStorage idempotentStorage2, IdempotentRequest request, Supplier<T> execute, Supplier<T> fail) {
        String firstValue = idempotentStorage1.getValue(request.getKey());
        String secondValue = null;
        if (idempotentStorage2 != null) {
            secondValue = idempotentStorage2.getValue(request.getKey());
        }
        // 一级和二级存储中都没有数据，表示可以继续执行
        if (!StringUtils.hasText(firstValue) && !StringUtils.hasText(secondValue)) {
            T executeResult = execute.get();

            idempotentStorage1.setValue(request.getKey(), idempotentDefaultValue, request.getFirstLevelExpireTime(), request.getTimeUnit());
            if (idempotentStorage2 != null) {
                idempotentStorage2.setValue(request.getKey(), idempotentDefaultValue, request.getSecondLevelExpireTime(), request.getTimeUnit());
            }

            return executeResult;
        }

        return fail.get();
    }

    @Override
    public void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, ReadWriteTypeEnum readWriteType, Runnable execute, Runnable fail) {

    }

    @Override
    public void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Runnable execute, Runnable fail) {

    }

    @Override
    public void execute(IdempotentRequest request, Runnable execute, Runnable fail) {

    }
}
