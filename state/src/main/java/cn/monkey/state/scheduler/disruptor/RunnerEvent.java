package cn.monkey.state.scheduler.disruptor;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class RunnerEvent {
    private volatile Runnable runnable;

    static AtomicReferenceFieldUpdater<RunnerEvent, Runnable> fieldUpdater =
            AtomicReferenceFieldUpdater.newUpdater(RunnerEvent.class, Runnable.class, "runnable");

    public Runnable getRunnable() {
        return fieldUpdater.getAndSet(this, null);
    }

    public void setRunnable(Runnable runnable) {
        if (!fieldUpdater.compareAndSet(this, null, runnable)) {
            throw new IllegalArgumentException();
        }
    }
}
