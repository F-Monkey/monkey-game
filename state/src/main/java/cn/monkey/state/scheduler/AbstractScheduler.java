package cn.monkey.state.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractScheduler implements Scheduler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final long id;

    protected final ThreadFactory threadFactory;

    protected final Thread t;

    protected final AtomicBoolean isStarted;

    public AbstractScheduler(long id, ThreadFactory threadFactory) {
        this.id = id;
        this.threadFactory = threadFactory;
        this.isStarted = new AtomicBoolean(false);
        this.t = this.newThread();
    }

    protected Thread newThread() {
        return this.threadFactory.newThread(() -> {
            log.info("id: {} start", id());
        });
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public void start() {
        if (this.isStarted.compareAndSet(false, true)) {
            log.info("{} is start", this.id());
            this.t.start();
        }
    }

    @Override
    public boolean isStarted() {
        return this.isStarted.get();
    }

    @Override
    public void stop() {
        if (this.isStarted.compareAndSet(true, false) && !this.t.isInterrupted()) {
            this.t.interrupt();
        }
    }
}
