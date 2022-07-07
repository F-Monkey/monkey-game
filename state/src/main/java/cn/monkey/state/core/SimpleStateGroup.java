package cn.monkey.state.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleStateGroup<E> extends AbstractStateGroup<E> {

    public SimpleStateGroup(String id, StateContext stateContext, boolean canAutoUpdate) {
        super(id, stateContext, canAutoUpdate);
    }

    @Override
    protected BlockingQueue<E> createEventQueue() {
        return new ArrayBlockingQueue<>(1024);
    }

    @Override
    protected Map<String, State<E>> createStateMap() {
        return new HashMap<>();
    }
}
