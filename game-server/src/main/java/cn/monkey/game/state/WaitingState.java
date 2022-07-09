package cn.monkey.game.state;

import cn.monkey.commons.utils.Timer;
import cn.monkey.game.core.Player;
import cn.monkey.game.core.UserCmdPair;
import cn.monkey.game.data.User;
import cn.monkey.proto.CmdType;
import cn.monkey.proto.Command;
import cn.monkey.state.core.StateInfo;

import java.util.Collection;

public class WaitingState extends GameState {

    public static final String CODE  = "waiting";

    private Long startCountDownTime;

    private final long maxCountDownTime;

    public WaitingState(Timer timer, GameStateGroup stateGroup,
                        long maxCountDownTime) {
        super(timer, stateGroup);
        this.maxCountDownTime = maxCountDownTime;
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    protected void onInit() {
        super.onInit();
    }

    @Override
    public void fireEvent(UserCmdPair playerCmdPair) throws Exception {
        Command.Package pkg = playerCmdPair.getPkg();
        int cmdType = pkg.getCmdType();
        switch (cmdType){
            case CmdType.ENTER:
                super.enter(playerCmdPair.getUser(),pkg);
                return;
            case CmdType.READY:
                this.ready(playerCmdPair.getUser(), pkg);
                return;
            default:
                // todo unsupported cmdType
        }
    }

    private void ready(User user, Command.Package pkg) {

    }

    @Override
    public void update(StateInfo stateInfo) throws Exception {
        GameStateContext stateContext = getStateContext();
        if(!stateContext.isFull()){
            return;
        }
        if(this.startCountDownTime == null){
            this.startCountDownTime = super.timer.getCurrentTimeMs();
        }
        Collection<Player> players = stateContext.getPlayers();
        for(Player player: players){
            if(!player.isReady()){
                if(super.timer.getCurrentTimeMs() - this.startCountDownTime >= this.maxCountDownTime){
                    this.removeUnReadyPlayer();
                }
                return;
            }
        }
        stateInfo.isFinish = true;
    }

    private void removeUnReadyPlayer() {
        // TODO
    }

    @Override
    public String finish() throws Exception {
        return StartState.CODE;
    }
}
