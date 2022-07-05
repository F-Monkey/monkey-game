package cn.monkey.server.netty.tcp;

import cn.monkey.proto.Command;
import cn.monkey.server.Dispatcher;
import cn.monkey.server.Filter;
import cn.monkey.server.Session;
import cn.monkey.server.SessionManager;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ChannelHandler.Sharable
public class ProtoBufWebSocketChannelHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final SessionManager<ChannelHandlerContext> sessionManager;

    protected final List<Filter<Command.Package>> filters;

    protected final Dispatcher<Command.Package> dispatcher;

    public ProtoBufWebSocketChannelHandler(SessionManager<ChannelHandlerContext> sessionManager,
                                           List<Filter<Command.Package>> filters,
                                           Dispatcher<Command.Package> dispatcher) {
        this.sessionManager = sessionManager;
        this.filters = filters;
        this.dispatcher = dispatcher;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg.content());
        Command.Package pkg;
        try {
            pkg = Command.Package.parseFrom(byteBuf.array());
        } catch (InvalidProtocolBufferException e) {
            log.error("data parse error:\n", e);
            return;
        } finally {
            ReferenceCountUtil.release(byteBuf);
        }
        Session session = this.sessionManager.findOrCreate(ctx);
        if (this.filters != null && filters.size() > 0) {
            for (Filter<Command.Package> f : this.filters) {
                if (!f.filter(session, pkg)) {
                    return;
                }
            }
        }
        if (this.dispatcher != null) {
            this.dispatcher.dispatch(session, pkg);
        }
    }
}
