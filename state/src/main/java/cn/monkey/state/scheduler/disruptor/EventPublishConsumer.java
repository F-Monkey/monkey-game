package cn.monkey.state.scheduler.disruptor;

import com.lmax.disruptor.EventHandler;

class EventPublishConsumer implements EventHandler<RunnerEvent> {
    @Override
    public void onEvent(RunnerEvent runnerEvent, long l, boolean b) throws Exception {
        Runnable runnable = runnerEvent.getRunnable();
        if (null != runnable) {
            runnable.run();
        }
    }
}
