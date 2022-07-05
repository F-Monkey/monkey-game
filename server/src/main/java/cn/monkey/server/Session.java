package cn.monkey.server;

import io.netty.util.AttributeKey;

import java.io.Closeable;
import java.io.IOException;

public interface Session extends Closeable {

    String id();

    <T> T setAttribute(String key, T val);

    <T> T getAttribute(String key);

    void write(Object data);

    boolean isActive();

    @Override
    void close() throws IOException;
}
