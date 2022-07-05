package cn.monkey.game.core;

import cn.monkey.game.data.User;
import cn.monkey.proto.Command;
import com.google.common.base.Preconditions;

public class UserCmdPair {

    private final User user;
    private final Command.Package pkg;

    public UserCmdPair(User user, Command.Package pkg) {
        Preconditions.checkNotNull(pkg);
        Preconditions.checkNotNull(user);
        this.user = user;
        this.pkg = pkg;
    }

    public Command.Package getPkg() {
        return pkg;
    }

    public User getUser() {
        return this.user;
    }
}
