package cn.monkey.game.config;

import cn.monkey.commons.utils.Timer;
import cn.monkey.game.core.UserCmdPair;
import cn.monkey.state.core.SimpleStateGroupFactory;
import cn.monkey.state.core.SimpleStateGroupPool;
import cn.monkey.state.core.StateGroupFactory;
import cn.monkey.state.core.StateGroupPool;
import cn.monkey.state.scheduler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StateConfig {

    @Bean
    Timer timer(){
        return new Timer() {
        };
    }

    @Bean
    StateGroupSchedulerFactoryConfig stateGroupSchedulerFactoryConfig() {
        return StateGroupSchedulerFactoryConfig.newBuilder().build();
    }

    @Bean
    SchedulerManagerConfig schedulerManagerConfig() {
        return SchedulerManagerConfig.newBuilder().build();
    }

    @Bean
    StateGroupFactory<UserCmdPair> stateGroupFactory(Timer timer) {
        return new SimpleStateGroupFactory<>(timer);
    }

    @Bean
    StateGroupPool<UserCmdPair> stateGroupPool(StateGroupFactory<UserCmdPair> stateGroupFactory) {
        return new SimpleStateGroupPool<>(stateGroupFactory);
    }


    @Bean
    StateGroupSchedulerFactory stateGroupSchedulerFactory(StateGroupSchedulerFactoryConfig stateGroupSchedulerFactoryConfig) {
        return new SimpleStateGroupSchedulerFactory(stateGroupSchedulerFactoryConfig);
    }

    @Bean
    EventPublishSchedulerFactory eventPublishSchedulerFactory() {
        return new SimpleEventPublishSchedulerFactory();
    }

    @Bean
    SchedulerManager<UserCmdPair> schedulerManager(StateGroupPool<UserCmdPair> stateGroupPool,
                                                   StateGroupSchedulerFactory stateGroupSchedulerFactory,
                                                   EventPublishSchedulerFactory eventPublishSchedulerFactory,
                                                   SchedulerManagerConfig schedulerManagerConfig) {
        return new SimpleSchedulerManager<>(stateGroupPool, stateGroupSchedulerFactory, eventPublishSchedulerFactory, schedulerManagerConfig);
    }
}
