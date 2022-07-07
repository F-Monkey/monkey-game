package cn.monkey.state.scheduler.strategy;

import java.util.concurrent.locks.LockSupport;

class SleepingWaitingStrategy implements WaitingStrategy {

    private final long waitTime;

    SleepingWaitingStrategy(long waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public void await() {
        LockSupport.parkNanos(this.waitTime);
    }
}
