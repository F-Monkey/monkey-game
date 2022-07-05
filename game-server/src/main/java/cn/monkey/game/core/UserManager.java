package cn.monkey.game.core;

import cn.monkey.commons.bean.Refreshable;
import cn.monkey.commons.utils.Timer;
import cn.monkey.game.data.User;
import cn.monkey.game.repository.UserRepository;
import cn.monkey.server.Session;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager implements Refreshable {

    private volatile ConcurrentHashMap<String, User> userMap;

    private final Timer timer;

    private final UserRepository userRepository;

    public UserManager(Timer timer,
                       UserRepository userRepository) {
        this.timer = timer;
        this.userRepository = userRepository;
    }


    public User findOrCreate(Session session, String token) {
        User user = this.userMap.compute(token, (k, v) -> {
            if (v == null) {
                v = this.userRepository.get(k);
            }
            v.setSession(session);
            v.setTimer(timer);
            return v;
        });
        user.refreshLastOperateTime();
        return user;
    }

    @Override
    public void refresh() {
        final ConcurrentHashMap<String, User> userMap = this.userMap;
        Iterator<Map.Entry<String, User>> iterator = userMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, User> next = iterator.next();
            User value = next.getValue();
            if (!value.isActive()) {
                iterator.remove();
            }
        }
    }
}
