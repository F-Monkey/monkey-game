package cn.monkey.game.data;

import cn.monkey.commons.utils.Timer;
import cn.monkey.proto.Command;
import cn.monkey.server.Session;
import org.springframework.lang.NonNull;

import java.io.Serializable;

public class User implements Serializable {

    private final String username;
    private final String uid;

    private Session session;

    private Timer timer;

    private volatile long lastOperateTime;

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void setSession(@NonNull Session session) {
        this.session = session;
    }

    public void write(Command.PackageGroup packageGroup) {
        this.session.write(packageGroup);
    }

    public String getUid() {
        return uid;
    }

    public boolean isActive() {
        return this.session.isActive();
    }

    public void refreshLastOperateTime() {
        this.lastOperateTime = this.timer.getCurrentTimeMs();
    }

    public long getLastOperateTime() {
        return lastOperateTime;
    }

    public String getUsername() {
        return username;
    }
}
