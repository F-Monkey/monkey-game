package cn.monkey.state.scheduler.util;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class DelegateBlockQueue<E> implements Queue<E>{

    private final BlockingQueue<E> delegate;

    public DelegateBlockQueue(BlockingQueue<E> queue){
        this.delegate = queue;
    }

    @Override
    public boolean offer(E e) {
        return this.delegate.offer(e);
    }

    @Override
    public E poll() {
        return this.delegate.poll();
    }

    @Override
    public void drainTo(Collection<E> es) {
        this.delegate.drainTo(es);
    }
}
