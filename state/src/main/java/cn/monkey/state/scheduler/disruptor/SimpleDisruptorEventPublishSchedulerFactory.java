package cn.monkey.state.scheduler.disruptor;

import cn.monkey.state.scheduler.EventPublishScheduler;
import cn.monkey.state.scheduler.EventPublishSchedulerFactory;
import cn.monkey.state.scheduler.strategy.WaitingStrategy;
import com.google.common.base.Preconditions;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SimpleDisruptorEventPublishSchedulerFactory implements EventPublishSchedulerFactory {

    protected ThreadFactory threadFactory = Executors.defaultThreadFactory();

    @Override
    public EventPublishScheduler create(long id) {
        return new SimpleDisruptorEventPublishScheduler(id, this.threadFactory);
    }

    @Override
    public void setThreadFactory(ThreadFactory threadFactory) {
        Preconditions.checkNotNull(threadFactory);
        this.threadFactory = threadFactory;
    }

    @Override
    public void setWaitingStrategy(WaitingStrategy waitingStrategy) {
        // do noting
    }
}
