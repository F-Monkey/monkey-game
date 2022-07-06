package cn.monkey.state.scheduler.disruptor;

import com.lmax.disruptor.EventFactory;

class EventPublisherFactory implements EventFactory<RunnerEvent> {
    @Override
    public RunnerEvent newInstance() {
        return new RunnerEvent();
    }
}
