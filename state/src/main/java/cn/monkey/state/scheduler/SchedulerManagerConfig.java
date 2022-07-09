package cn.monkey.state.scheduler;

public class SchedulerManagerConfig {

    private SchedulerManagerConfig(){}
    private static final int DEFAULT_STATE_GROUP_SCHEDULER_MAX_SIZE = 10;

    private static final int DEFAULT_STATE_GROUP_SCHEDULER_CORE_SIZE = 4;
    private static final int DEFAULT_EVENT_PUBLISH_SCHEDULER_SIZE = 1 << 2;

    private int stateGroupSchedulerSize = DEFAULT_STATE_GROUP_SCHEDULER_MAX_SIZE;
    private int eventPublisherSchedulerSize = DEFAULT_EVENT_PUBLISH_SCHEDULER_SIZE;

    private int stateGroupSchedulerCoreSize = DEFAULT_STATE_GROUP_SCHEDULER_CORE_SIZE;

    public int getEventPublisherSchedulerSize() {
        return eventPublisherSchedulerSize;
    }

    public int getStateGroupSchedulerSize() {
        return stateGroupSchedulerSize;
    }

    public int getStateGroupSchedulerCoreSize() {
        return stateGroupSchedulerCoreSize;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final SchedulerManagerConfig config;

        Builder() {
            config = new SchedulerManagerConfig();
        }

        public Builder stateGroupSchedulerSize(int stateGroupSchedulerSize) {
            this.config.stateGroupSchedulerSize = stateGroupSchedulerSize;
            return this;
        }

        public Builder eventPublisherSchedulerSize(int eventPublisherSchedulerSize) {
            this.config.eventPublisherSchedulerSize = eventPublisherSchedulerSize;
            return this;
        }

        public Builder stateGroupSchedulerCoreSize(int stateGroupSchedulerCoreSize){
            this.config.stateGroupSchedulerCoreSize = stateGroupSchedulerCoreSize;
            return this;
        }

        public SchedulerManagerConfig build() {
            return this.config;
        }
    }
}
