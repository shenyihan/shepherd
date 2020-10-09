package com.shepherd.distributed.idempotent.exception;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentException.java
 * @Description TODO
 * @createTime 2020年10月09日 10:43:00
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException(String message) {
        super(message);
    }
}
