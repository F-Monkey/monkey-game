package cn.monkey.state.scheduler;

import cn.monkey.commons.bean.Refreshable;
import cn.monkey.state.core.StateGroup;
import cn.monkey.state.scheduler.strategy.WaitingStrategy;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class SingleStateGroupScheduler extends EventLoopScheduler implements StateGroupScheduler, Refreshable {

    protected volatile StateGroup<?> stateGroup;

    @SuppressWarnings("rawtypes")
    protected static final AtomicReferenceFieldUpdater<SingleStateGroupScheduler, StateGroup> STATE_GROUP_UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(SingleStateGroupScheduler.class, StateGroup.class, "stateGroup");

    public SingleStateGroupScheduler(long id, WaitingStrategy waitingStrategy, ThreadFactory threadFactory) {
        super(id, waitingStrategy, threadFactory);
    }

    @Override
    public boolean isEmpty() {
        return this.stateGroup == null;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isFull() {
        return this.stateGroup != null;
    }

    @Override
    protected void execute() {
        if (null != this.stateGroup) {
            this.stateGroup.update();
        }
    }

    @Override
    public boolean tryAddStateGroup(StateGroup<?> stateGroup) {
        return STATE_GROUP_UPDATER.compareAndSet(this, null, stateGroup);
    }

    @Override
    public void refresh() {
        if (this.stateGroup.canClose()) {
            this.stateGroup = null;
        }
    }
}
