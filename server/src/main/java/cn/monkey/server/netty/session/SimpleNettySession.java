package cn.monkey.server.netty.session;

import cn.monkey.server.Session;
import cn.monkey.server.SessionUtil;
import com.google.protobuf.MessageLite;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SimpleNettySession implements Session {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ChannelHandlerContext ctx;

    public SimpleNettySession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String id() {
        return SessionUtil.getId(this.ctx);
    }

    @Override
    public <T> T setAttribute(String key, T val) {
        AttributeKey<T> attributeKey = AttributeKey.newInstance(key);
        return this.ctx.channel().attr(attributeKey).getAndSet(val);
    }

    @Override
    public <T> T getAttribute(String key) {
        AttributeKey<T> attributeKey = AttributeKey.newInstance(key);
        return this.ctx.channel().attr(attributeKey).get();
    }

    @Override
    public void write(Object data) {
        if (null == data) {
            return;
        }
        if (data instanceof BinaryWebSocketFrame) {
            this.ctx.writeAndFlush(data);
            return;
        }
        if (data instanceof MessageLite) {
            byte[] bytes = ((MessageLite) data).toByteArray();
            BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(Unpooled.copiedBuffer(bytes));
            this.ctx.writeAndFlush(binaryWebSocketFrame);
            return;
        }
        log.error("invalid data type: {}", data.getClass());
    }

    @Override
    public boolean isActive() {
        return this.ctx.channel().isActive();
    }

    @Override
    public void close() throws IOException {
        this.ctx.channel().close();
    }
}
