package cn.monkey.game.state;

import cn.monkey.game.core.UserCmdPair;
import cn.monkey.state.core.SimpleStateGroup;

public class GameStateGroup extends SimpleStateGroup<UserCmdPair> {
    public GameStateGroup(String id, GameStateContext stateContext, boolean canAutoUpdate) {
        super(id, stateContext, canAutoUpdate);
    }

    @Override
    public GameStateContext getStateContext() {
        return (GameStateContext)super.getStateContext();
    }
}
