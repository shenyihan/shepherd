package com.shepherd.utils;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName SPELUtil.java
 * @Description TODO
 * @createTime 2020年10月09日 10:09:00
 */
public class SPELUtil {

    /**
     * 获取幂等的key, 支持SPEL表达式
     * @param key
     * @param method
     * @param args
     * @return
     */
    public static String parseKey(String key, Method method, Object[] args){
        LocalVariableTableParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = nameDiscoverer.getParameterNames(method);

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for(int i = 0;i < paraNameArr.length; i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        try {
            return parser.parseExpression(key).getValue(context, String.class);
        } catch (SpelEvaluationException e) {
            throw new RuntimeException("SPEL表达式解析错误", e);
        }
    }
}
