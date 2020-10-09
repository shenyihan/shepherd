package com.shepherd.distributed.idempotent.factory;

import com.shepherd.distributed.idempotent.db.IdempotentStorage;
import com.shepherd.distributed.idempotent.enums.StorageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentStorageFactory.java
 * @Description TODO
 * @createTime 2020年10月02日 16:58:00
 */
public class IdempotentStorageFactory {

    @Autowired
    private List<IdempotentStorage> idempotentStorages;

    public IdempotentStorage getIdempotentStorage(StorageTypeEnum type){
        Optional<IdempotentStorage> idempotentStorage = idempotentStorages.stream().filter(o -> o.type() == type).findAny();
        return idempotentStorage.orElseThrow(NullPointerException::new);
    }
}
