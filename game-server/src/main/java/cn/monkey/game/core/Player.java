package cn.monkey.game.core;

import cn.monkey.game.data.User;
import cn.monkey.proto.Command;

public class Player {
    private User user;

    private boolean isReady;

    public Player(User user){
        this.user = user;
        this.isReady = true;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return this.user.getUid();
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public String getUid() {
        return this.user.getUid();
    }

    public void write(Command.PackageGroup packageGroup) {
        this.user.write(packageGroup);
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public boolean isReady() {
        return isReady;
    }
}
