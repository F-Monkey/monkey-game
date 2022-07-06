package cn.monkey.state.scheduler.disruptor;

import cn.monkey.state.scheduler.EventPublishScheduler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

public class SimpleDisruptorEventPublishScheduler implements EventPublishScheduler {

    protected final long id;

    protected final Disruptor<RunnerEvent> disruptor;

    public SimpleDisruptorEventPublishScheduler(long id, ThreadFactory threadFactory) {
        this.id = id;
        EventPublisherFactory eventPublisherFactory = new EventPublisherFactory();
        this.disruptor = new Disruptor<>(eventPublisherFactory, 1 << 10, threadFactory, ProducerType.SINGLE, new BlockingWaitStrategy());
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
        this.disruptor.start();
    }

    @Override
    public boolean isStarted() {
        return this.disruptor.hasStarted();
    }

    @Override
    public void stop() {
        this.disruptor.shutdown();
    }
}
