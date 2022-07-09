package cn.monkey.state.scheduler;

import cn.monkey.commons.bean.Refreshable;
import cn.monkey.state.core.StateGroup;
import cn.monkey.state.scheduler.strategy.WaitingStrategy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class SimpleStateGroupScheduler extends EventLoopScheduler implements StateGroupScheduler, Refreshable {

    protected volatile ConcurrentHashMap<String, StateGroup<?>> stateGroupMap;

    protected volatile StateGroup<?> currentAddStateGroup;

    protected final int maxSize;

    @SuppressWarnings("rawtypes")
    protected static final AtomicReferenceFieldUpdater<SimpleStateGroupScheduler, StateGroup> STATE_GROUP_UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(SimpleStateGroupScheduler.class, StateGroup.class, "currentAddStateGroup");

    protected SimpleStateGroupScheduler(long id,
                                        WaitingStrategy waitingStrategy,
                                        ThreadFactory threadFactory,
                                        int maxSize) {
        super(id, waitingStrategy, threadFactory);
        this.maxSize = maxSize;
        this.stateGroupMap = new ConcurrentHashMap<>();
    }

    @Override
    protected final void execute() {
        for (StateGroup<?> stateGroup : this.stateGroupMap.values()) {
            try {
                stateGroup.update();
            } catch (Exception e) {
                log.error("stateGroup: {} update error", stateGroup.id());
            }
        }
    }

    @Override
    public boolean tryAddStateGroup(StateGroup<?> stateGroup) {
        if (!this.isStarted() || !STATE_GROUP_UPDATER.compareAndSet(this, null, stateGroup)) {
            return false;
        }
        final ConcurrentHashMap<String, StateGroup<?>> stateGroupMap = this.stateGroupMap;
        stateGroupMap.put(this.currentAddStateGroup.id(), this.currentAddStateGroup);
        this.stateGroupMap = stateGroupMap;
        this.currentAddStateGroup = null;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.currentAddStateGroup == null && this.stateGroupMap.isEmpty();
    }

    @Override
    public int size() {
        return (this.currentAddStateGroup == null ? 0 : 1) + this.stateGroupMap.size();
    }

    @Override
    public boolean isFull() {
        return this.size() >= this.maxSize;
    }

    @Override
    public void refresh() {
        final ConcurrentHashMap<String, StateGroup<?>> stateGroupMap = new ConcurrentHashMap<>();
        for (StateGroup<?> stateGroup : this.stateGroupMap.values()) {
            if (stateGroup.canClose()) {
                continue;
            }
            stateGroupMap.put(stateGroup.id(), stateGroup);
        }
        this.stateGroupMap = stateGroupMap;
    }
}
