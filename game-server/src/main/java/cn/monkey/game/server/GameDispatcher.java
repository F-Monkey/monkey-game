package cn.monkey.game.server;

import cn.monkey.game.core.UserCmdPair;
import cn.monkey.game.core.UserManager;
import cn.monkey.game.utils.GameCmdUtil;
import cn.monkey.proto.CmdType;
import cn.monkey.proto.Command;
import cn.monkey.proto.Game;
import cn.monkey.server.Dispatcher;
import cn.monkey.server.Session;
import cn.monkey.state.scheduler.SchedulerManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

public class GameDispatcher implements Dispatcher<Command.Package> {

    static final String USER_KEY = "user";
    private final LoadingCache<String, ReentrantLock> lockCache;
    private final SchedulerManager<UserCmdPair> schedulerManager;
    private final UserManager playerManager;
    private final Scheduler scheduler;

    private final Scheduler loginScheduler;

    public GameDispatcher(SchedulerManager<UserCmdPair> schedulerManager,
                          UserManager playerManager) {
        this.schedulerManager = schedulerManager;
        this.playerManager = playerManager;
        this.lockCache = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofSeconds(2))
                .build(new CacheLoader<>() {
                    @Override
                    @NonNull
                    public ReentrantLock load(@NonNull String key) throws Exception {
                        return new ReentrantLock();
                    }
                });
        this.scheduler = Schedulers.newSingle("dispatcher");
        this.loginScheduler = Schedulers.newParallel("login", 4);
    }


    @Override
    public void dispatch(Session session, Command.Package pkg) {
        int cmdType = pkg.getCmdType();
        ReentrantLock reentrantLock = this.lockCache.getUnchecked(session.id());
        if (!reentrantLock.tryLock()) {
            return;
        }
        try {
            if (cmdType == CmdType.LOGIN) {
                Mono.just(pkg)
                        .map(p -> {
                            try {
                                return Game.Session.parseFrom(p.getContent());
                            } catch (InvalidProtocolBufferException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(Game.Session::getToken)
                        .map(token -> this.playerManager.findOrCreate(session, token))
                        .doOnNext(user -> session.setAttribute(USER_KEY, user))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("invalid token")))
                        .doOnError(e -> session.write(GameCmdUtil.error(e)))
                        .subscribeOn(this.loginScheduler)
                        .subscribe();
            } else {
                Mono.just(pkg)
                        .map(Command.Package::getGroupId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("groupId is required")))
                        .doOnNext(t -> this.schedulerManager.addEvent(t, new UserCmdPair(session.getAttribute(USER_KEY), pkg)))
                        .doOnError(e -> session.write(GameCmdUtil.error(e)))
                        .subscribeOn(this.scheduler)
                        .subscribe();
            }
        } finally {
            reentrantLock.unlock();
        }
    }
}
