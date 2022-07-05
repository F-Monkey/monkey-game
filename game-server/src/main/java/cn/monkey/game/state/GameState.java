package cn.monkey.game.state;

import cn.monkey.commons.utils.Timer;
import cn.monkey.game.core.UserCmdPair;
import cn.monkey.game.data.User;
import cn.monkey.game.utils.GameCmdUtil;
import cn.monkey.proto.CmdType;
import cn.monkey.proto.Command;
import cn.monkey.proto.Game;
import cn.monkey.state.core.OncePerInitState;
import com.google.common.base.Strings;
import com.google.protobuf.InvalidProtocolBufferException;

public abstract class GameState extends OncePerInitState<UserCmdPair> {

    public GameState(Timer timer, GameStateGroup stateGroup) {
        super(timer, stateGroup);
    }

    @Override
    public void fireEvent(UserCmdPair playerCmdPair) throws Exception {
        Command.Package pkg = playerCmdPair.getPkg();
        int cmdType = pkg.getCmdType();
        if (cmdType == CmdType.ENTER) {
            this.enter(playerCmdPair.getUser(), pkg);
            return;
        }
        this.handleCmd(playerCmdPair.getUser(), pkg);
    }

    @Override
    public void fireEventOnError(UserCmdPair userCmdPair, Exception e) {
        User user = userCmdPair.getUser();
        if (e instanceof InvalidProtocolBufferException) {
            user.write(GameCmdUtil.error(new IllegalArgumentException("bad package")));
            return;
        }
        user.write(GameCmdUtil.error(e));
    }

    @Override
    protected void onInit() {
        // do nothing here
    }


    @Override
    public GameStateGroup getStateGroup() {
        return (GameStateGroup) super.getStateGroup();
    }

    @Override
    public GameStateContext getStateContext() {
        return this.getStateGroup().getStateContext();
    }

    protected void enter(User user, Command.Package pkg) throws InvalidProtocolBufferException {
        GameStateContext stateContext = this.getStateContext();
        String password = stateContext.getPassword();
        if (!Strings.isNullOrEmpty(password)) {
            Game.Enter enter = Game.Enter.parseFrom(pkg.getContent());
            String enterPassword = enter.getPassword();
            if (!enterPassword.equals(password)) {
                log.error("invalid password enter");
                user.write(GameCmdUtil.enterFail("bad password"));
                return;
            }
        }
        if (!stateContext.tryAddPlayer(user)) {
            user.write(GameCmdUtil.enterFail("room is full"));
            return;
        }
        user.write(GameCmdUtil.enterSuccess(stateContext));
    }

    protected void handleCmd(User user, Command.Package pkg) {

    }
}
