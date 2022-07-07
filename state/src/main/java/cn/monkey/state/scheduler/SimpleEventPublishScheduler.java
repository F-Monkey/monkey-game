package cn.monkey.state.scheduler;

import cn.monkey.state.scheduler.strategy.WaitingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

public class SimpleEventPublishScheduler extends EventLoopScheduler implements EventPublishScheduler {

    protected final BlockingQueue<Runnable> taskQueue;

    public SimpleEventPublishScheduler(long id, WaitingStrategy waitingStrategy, ThreadFactory threadFactory) {
        super(id, waitingStrategy, threadFactory);
        this.taskQueue = new ArrayBlockingQueue<>(1024);
    }

    @Override
    protected final void execute() {
        if (this.taskQueue.isEmpty()) {
            return;
        }
        List<Runnable> list = new ArrayList<>(this.taskQueue.size());
        this.taskQueue.drainTo(list);
        for (Runnable r : list) {
            r.run();
        }
    }

    @Override
    public void publish(Runnable r) {
        if (this.taskQueue.offer(r)) {
            this.waitingStrategy.signalAllWhenBlocking();
            return;
        }
        log.error("id: {} eventQueue is full", this.id());
    }
}
