package com.shepherd.context;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName ContextHolder.java
 * @Description TODO
 * @createTime 2020年10月09日 10:31:00
 */
public class ContextHolder {

    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            return new RequestContext();
        }
    };


    public static RequestContext getCurrentContext() {
        return contextHolder.get();
    }

    public static void setCurrentContext(RequestContext requestContext) {
        contextHolder.set(requestContext);
    }


    public static void clearCurrentContext() {
        contextHolder.remove();
    }
}
