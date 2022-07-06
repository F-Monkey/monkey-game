package cn.monkey.state.scheduler.disruptor;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;

public class EventPublishProcessor implements EventProcessor {
    @Override
    public Sequence getSequence() {
        return null;
    }

    @Override
    public void halt() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void run() {

    }
}
