package cn.monkey.game.core;

import cn.monkey.commons.bean.Refreshable;
import cn.monkey.commons.utils.Timer;
import cn.monkey.data.User;
import cn.monkey.proto.Command;
import cn.monkey.server.Session;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager implements Refreshable {

    private volatile ConcurrentHashMap<String, Player> playerMap;

    private final Timer timer;

    public PlayerManager(Timer timer) {
        this.timer = timer;
        this.playerMap = new ConcurrentHashMap<>();
    }


    public Player findOrCreate(Session session, User user) {
        final ConcurrentHashMap<String, Player> playerMap = this.playerMap;
        Player p = playerMap.compute(user.getUid(), (s, player) -> {
            if (null == player) {
                return new Player(session, user, timer);
            }
            player.setSession(session);
            return player;
        });
        p.refreshLastOperateTime();
        this.playerMap = playerMap;
        return p;
    }

    @Override
    public void refresh() {
        final ConcurrentHashMap<String, Player> playerMap = this.playerMap;
        Iterator<Map.Entry<String, Player>> iterator = playerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Player> next = iterator.next();
            Player value = next.getValue();
            if (!value.isActive()) {
                iterator.remove();
            }
        }
    }
}
