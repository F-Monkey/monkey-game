package cn.monkey.game.state;

import cn.monkey.commons.utils.Timer;

public class StartState extends GameState {

    public static final String CODE = "start";

    public StartState(Timer timer, GameStateGroup stateGroup) {
        super(timer, stateGroup);
    }

    @Override
    protected void onInit() {
        // todo introduce game info
    }

    @Override
    public String code() {
        return CODE;
    }


    @Override
    public String finish() throws Exception {
        return null;
    }
}
