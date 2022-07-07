package cn.monkey.state.scheduler.util;

import com.lmax.disruptor.RingBuffer;

import java.util.Collection;

public class RingBufferQueue<E> implements Queue<E>{


    public RingBufferQueue(RingBuffer<E> ringBuffer){
    }


    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public void drainTo(Collection<E> es) {

    }
}
