package io.playqd.mediaserver.service.upnp.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public abstract class ActionContext<R> {

    private final Map<String, Object> headers;

    protected ActionContext() {
        this(Collections.emptyMap());
    }

    protected ActionContext(Map<String, Object> headers) {
        this.headers = headers;
    }

    public abstract R getRequest();

    public void addHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void addHeaders(Map<String, ?> headers) {
        this.headers.putAll(headers);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHeader(String key) {
        return (T) headers.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHeader(String key, T defaultValue) {
        return Optional.ofNullable(headers.get(key))
                .map( val -> (T) val)
                .orElse(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getRequiredHeader(String key) {
        return Optional.ofNullable(headers.get(key))
                .map(val -> (T) val)
                .orElseThrow(() -> new IllegalStateException(String.format("No value found for key: %s", key)));
    }

    public <T> T getRequiredHeader(String key, Class<T> expectedType) {
        return Optional.ofNullable(headers.get(key))
                .map(expectedType::cast)
                .orElseThrow(() -> new IllegalStateException(String.format("No value found for key: %s", key)));
    }
}
