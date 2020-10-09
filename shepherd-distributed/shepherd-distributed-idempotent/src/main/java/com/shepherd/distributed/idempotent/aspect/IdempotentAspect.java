package com.shepherd.distributed.idempotent.aspect;

import com.shepherd.context.ContextHolder;
import com.shepherd.distributed.idempotent.anotation.Idempotent;
import com.shepherd.distributed.idempotent.exception.IdempotentException;
import com.shepherd.distributed.idempotent.request.IdempotentRequest;
import com.shepherd.distributed.idempotent.service.IdempotentService;
import com.shepherd.utils.SPELUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName IdempotentAspect.java
 * @Description TODO 注解切面类
 * @createTime 2020年10月02日 16:00:00
 */
@Aspect
public class IdempotentAspect extends AbstractIdempotentAspectSupport{

    private IdempotentService idempotentService;

    public IdempotentAspect(IdempotentService idempotentService){
        this.idempotentService = idempotentService;
    }

    @Around(value = "@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinpoint, Idempotent idempotent) throws Throwable {
        Object[] args = joinpoint.getArgs();
        Method method = ((MethodSignature) joinpoint.getSignature()).getMethod();

        String key = "";
        if (StringUtils.hasText(idempotent.spelKey())) {
            key = SPELUtil.parseKey(idempotent.spelKey(), method, args);
        } else {
            //如果用户未设置key，采用默认的幂等key
            key = ContextHolder.getCurrentContext().get("globalIdempotentId");
        }

        String userInputKey = idempotent.value();
        if (!StringUtils.hasText(userInputKey)) {
            userInputKey = method.getName();
        }
        String idempotentKey = userInputKey + ":" + key;

        IdempotentRequest request = IdempotentRequest.builder().key(idempotentKey)
                .firstLevelExpireTime(idempotent.firstLevelExpireTime())
                .secondLevelExpireTime(idempotent.secondLevelExpireTime())
                .timeUnit(idempotent.timeUnit())
                .lockExpireTime(idempotent.lockExpireTime())
                .readWriteType(idempotent.readWriteType())
                .build();

        try {
            return idempotentService.execute(request, () -> {
                try {
                    return joinpoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                throw new IdempotentException("重复请求");
            });
        } catch (IdempotentException ex) {
            return handleIdempotentException(joinpoint, idempotent, ex);
        }
    }
}
