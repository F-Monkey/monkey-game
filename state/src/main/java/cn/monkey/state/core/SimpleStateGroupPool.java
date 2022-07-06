package cn.monkey.state.core;

import cn.monkey.commons.bean.Refreshable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStateGroupPool<E> implements StateGroupPool<E>, Refreshable {

    protected final StateGroupFactory<E> stateGroupFactory;
    protected volatile ConcurrentHashMap<String, StateGroup<E>> stateGroupMap;

    public SimpleStateGroupPool(StateGroupFactory<E> stateGroupFactory) {
        this.stateGroupMap = new ConcurrentHashMap<>();
        this.stateGroupFactory = stateGroupFactory;
    }

    @Override
    public FetchStateGroup<E> findOrCreate(String id) {
        boolean[] isNew = {false};
        final ConcurrentHashMap<String, StateGroup<E>> stateGroupMap = this.stateGroupMap;
        StateGroup<E> stateGroup = stateGroupMap.computeIfAbsent(id, (key) -> {
            isNew[0] = true;
            return this.stateGroupFactory.create(key);
        });
        this.stateGroupMap = stateGroupMap;
        return new FetchStateGroup<>(isNew[0], stateGroup);
    }

    @Override
    public void refresh() {
        final ConcurrentHashMap<String, StateGroup<E>> stateGroupMap = this.stateGroupMap;
        Iterator<Map.Entry<String, StateGroup<E>>> iterator = stateGroupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, StateGroup<E>> next = iterator.next();
            StateGroup<E> value = next.getValue();
            if (value.canClose()) {
                value.close();
            }
            iterator.remove();
        }
    }
}
