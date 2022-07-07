package cn.monkey.state.scheduler.disruptor;

import cn.monkey.state.scheduler.EventPublishScheduler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleDisruptorEventPublishScheduler implements EventPublishScheduler {

    protected final long id;

    protected final AtomicBoolean isStarted;

    protected final Disruptor<RunnerEvent> disruptor;

    public SimpleDisruptorEventPublishScheduler(long id, ThreadFactory threadFactory) {
        this.id = id;
        this.isStarted = new AtomicBoolean(false);
        EventPublisherFactory eventPublisherFactory = new EventPublisherFactory();
        this.disruptor = new Disruptor<>(eventPublisherFactory, 1 << 10, threadFactory, ProducerType.SINGLE, new YieldingWaitStrategy());
        this.disruptor.handleEventsWith(new EventPublishConsumer());
    }

    @Override
    public void publish(Runnable event) {
        RingBuffer<RunnerEvent> ringBuffer = this.disruptor.getRingBuffer();
        long next = ringBuffer.next();
        try {
            RunnerEvent runnerEvent = ringBuffer.get(next);
            runnerEvent.setRunnable(event);
        } finally {
            ringBuffer.publish(next);
        }
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public void start() {
        if (this.isStarted.compareAndSet(false, true)) {
            this.disruptor.start();
        }
    }

    @Override
    public boolean isStarted() {
        return this.isStarted.get();
    }

    @Override
    public void stop() {
        if (this.isStarted.compareAndSet(true, false)) {
            this.disruptor.shutdown();
        }
    }
}