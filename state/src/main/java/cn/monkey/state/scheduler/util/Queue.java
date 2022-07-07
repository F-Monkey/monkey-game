package cn.monkey.state.scheduler.util;

import java.util.Collection;

public interface Queue <E> {
    boolean offer(E e);

    E poll();

    void drainTo(Collection<E> es);
}
