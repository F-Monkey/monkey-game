package cn.monkey.server;

public interface Dispatcher<Pkg> {
    void dispatch(Session session, Pkg pkg);
}
