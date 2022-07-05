package cn.monkey.game.state;

import cn.monkey.commons.utils.Timer;
import cn.monkey.game.core.UserCmdPair;
import cn.monkey.state.core.SimpleStateGroupFactory;
import cn.monkey.state.core.StateGroup;

public class GameStateGroupFactory extends SimpleStateGroupFactory<UserCmdPair> {
    public GameStateGroupFactory(Timer timer) {
        super(timer);
    }

    @Override
    public GameStateGroup create(String id) {
        GameStateContext gameStateContext = new GameStateContext(4);
        GameStateGroup gameStateGroup = new GameStateGroup(id, gameStateContext, false);
        gameStateGroup.addState(new StartState(super.timer, gameStateGroup));
        gameStateGroup.setStartState(StartState.CODE);
        return gameStateGroup;
    }
}
