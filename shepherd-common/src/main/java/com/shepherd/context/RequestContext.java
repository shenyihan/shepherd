package com.shepherd.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangxc
 * @version 1.0.0
 * @ClassName RequestContext.java
 * @Description TODO
 * @createTime 2020年10月09日 10:32:00
 */
public class RequestContext {
    private final Map<String, String> attributes = new HashMap<String, String>();

    public RequestContext add(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    public String get(String key) {
        return attributes.get(key);
    }

    public RequestContext remove(String key) {
        attributes.remove(key);
        return this;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}
