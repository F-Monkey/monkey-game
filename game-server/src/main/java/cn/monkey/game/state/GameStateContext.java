package cn.monkey.game.state;

import cn.monkey.game.core.Player;
import cn.monkey.game.data.User;
import cn.monkey.state.core.StateContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameStateContext implements StateContext {

    private final int playerSize;

    private final Map<String, Player> players;

    private String password;

    public GameStateContext(int playerSize) {
        this.players = new HashMap<>(playerSize);
        this.playerSize = playerSize;
    }

    public boolean tryAddPlayer(User user) {
        String uid = user.getUid();
        if (this.players.containsKey(uid)) {
            Player player = players.get(uid);
            player.setUser(user);
            return true;
        }
        if (this.players.size() >= this.playerSize) {
            return false;
        }
        this.players.put(uid, new Player(user));
        return true;
    }

    public Collection<Player> getPlayers(){
        return this.players.values();
    }

    public String getPassword() {
        return password;
    }

    public boolean isFull(){
        return this.players.size() >= this.playerSize;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
