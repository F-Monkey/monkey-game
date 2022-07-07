package cn.monkey.state.test;

import cn.monkey.commons.utils.Timer;
import cn.monkey.state.core.*;
import cn.monkey.state.scheduler.*;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StateTest2 {

    public static final String ENTER_CMD = "enter";

    public static final String ROCK_CMD = "rock";

    public static final String PAPER_CMD = "paper";

    public static final String SCISSORS_CMD = "scissors";


    static class PlayerCmdPair {
        public String name;
        public String cmd;
    }

    static class GameStateGroupFactory extends SimpleStateGroupFactory<StateTest.PlayerCmdPair> {

        public GameStateGroupFactory(Timer timer) {
            super(timer);
        }

        @Override
        public StateGroup<StateTest.PlayerCmdPair> create(String id) {
            StateTest.GameStateContext gameStateContext = new StateTest.GameStateContext();
            SimpleStateGroup<StateTest.PlayerCmdPair> stateGroup = new SimpleStateGroup<>(id, gameStateContext, false);
            stateGroup.addState(new StateTest.StartState(super.timer, stateGroup));
            stateGroup.addState(new StateTest.PlayingState(super.timer, stateGroup));
            stateGroup.setStartState(StateTest.StartState.CODE);
            return stateGroup;
        }
    }

    static class GameStateContext extends SimpleStateContext {

        public final List<StateTest.PlayerCmdPair> playerCmdPairs;

        public final Set<String> players;

        public GameStateContext() {
            this.playerCmdPairs = new ArrayList<>();
            players = new HashSet<>();
        }

        public void addGameData(StateTest.PlayerCmdPair playerCmdPair) {
            playerCmdPairs.add(playerCmdPair);
        }

        public void clear() {
            this.playerCmdPairs.clear();
        }

        public boolean tryAddPlayer(String playerName) {
            this.players.add(playerName);
            return true;
        }

        public List<StateTest.PlayerCmdPair> getPlayerCmdPairs() {
            return playerCmdPairs;
        }

        public Set<String> getPlayers() {
            return players;
        }
    }

    static abstract class GameState extends OncePerInitState<StateTest.PlayerCmdPair> {

        public GameState(Timer timer, StateGroup<StateTest.PlayerCmdPair> stateGroup) {
            super(timer, stateGroup);
        }

        @Override
        public StateTest.GameStateContext getStateContext() {
            return (StateTest.GameStateContext) super.getStateContext();
        }

        @Override
        protected void onInit() {

        }
    }

    static class StartState extends StateTest.GameState {

        private long startTime;

        @Override
        protected void onInit() {
            this.startTime = super.timer.getCurrentTimeMs();
        }

        public static String CODE = "start";

        public StartState(Timer timer, StateGroup<StateTest.PlayerCmdPair> stateGroup) {
            super(timer, stateGroup);
        }

        @Override
        public void fireEvent(StateTest.PlayerCmdPair playerCmdPair) throws Exception {
            String cmd = playerCmdPair.cmd;
            if (!ENTER_CMD.equals(cmd)) {
                return;
            }
            StateTest.GameStateContext stateContext = getStateContext();
            stateContext.tryAddPlayer(playerCmdPair.name);
            System.out.println("player: " + playerCmdPair.name + " enter");
        }

        @Override
        public void update(StateInfo stateInfo) throws Exception {
            StateTest.GameStateContext stateContext = getStateContext();
            Set<String> players = stateContext.getPlayers();
            if (players.size() >= 4) {
                stateInfo.isFinish = true;
            }
        }

        @Override
        public String code() {
            return CODE;
        }

        @Override
        public String finish() throws Exception {
            System.out.println("state: " + this.code() + " is finish");
            long l = super.timer.getCurrentTimeMs() - this.startTime;
            System.out.println("cost: " + l + " ms");
            return StateTest.PlayingState.CODE;
        }
    }

    static class PlayingState extends StateTest.GameState {

        public static final String CODE = "playing";

        public PlayingState(Timer timer, StateGroup<StateTest.PlayerCmdPair> stateGroup) {
            super(timer, stateGroup);
        }

        @Override
        public String code() {
            return CODE;
        }

        @Override
        public void fireEvent(StateTest.PlayerCmdPair playerCmdPair) throws Exception {
            StateTest.GameStateContext stateContext = getStateContext();
            stateContext.addGameData(playerCmdPair);
        }

        @Override
        public void update(StateInfo stateInfo) throws Exception {
            StateTest.GameStateContext stateContext = getStateContext();
            List<StateTest.PlayerCmdPair> playerCmdPairs = stateContext.getPlayerCmdPairs();
            if (playerCmdPairs.size() >= 4) {
                stateInfo.isFinish = true;
            }
        }

        @Override
        public String finish() throws Exception {
            System.out.println("state: " + this.code() + " is finished");
            StateTest.GameStateContext stateContext = getStateContext();
            List<StateTest.PlayerCmdPair> playerCmdPairs = stateContext.getPlayerCmdPairs();
            Map<String, List<String>> cmdMap = new HashMap<>();
            for (StateTest.PlayerCmdPair playerCmdPair : playerCmdPairs) {
                cmdMap.compute(playerCmdPair.cmd, (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                        v.add(playerCmdPair.name);
                    } else {
                        v.add(playerCmdPair.name);
                    }
                    return v;
                });
            }
            switch (cmdMap.size()) {
                case 1:
                case 3:
                    // 平局：
                    System.out.println("result:");
                    System.out.println(cmdMap);
                default:
                    List<String> rocks = cmdMap.get(ROCK_CMD);
                    List<String> papers = cmdMap.get(PAPER_CMD);
                    List<String> scissors = cmdMap.get(SCISSORS_CMD);
                    if (rocks == null) {
                        System.out.println("winners: ");
                        System.out.println(scissors);
                        System.out.println("losers: ");
                        System.out.println(papers);
                    } else if (papers == null) {
                        System.out.println("winners: ");
                        System.out.println(rocks);
                        System.out.println("losers: ");
                        System.out.println(scissors);
                    } else if (scissors == null) {
                        System.out.println("winners: ");
                        System.out.println(papers);
                        System.out.println("losers: ");
                        System.out.println(rocks);
                    }
            }
            stateContext.clear();
            return StateTest.StartState.CODE;
        }
    }

    public static void main(String[] args) {

        Timer timer = new Timer() {
        };

        StateGroupFactory<StateTest.PlayerCmdPair> stateGroupFactory = new StateTest.GameStateGroupFactory(timer);

        StateGroupPool<StateTest.PlayerCmdPair> stateGroupPool = new SimpleStateGroupPool<>(stateGroupFactory);

        StateGroupSchedulerFactoryConfig stateGroupSchedulerFactoryConfig =
                StateGroupSchedulerFactoryConfig.newBuilder()
                        .maxSize(1)
                        .updateFrequency(1)
                        .build();

        StateGroupSchedulerFactory stateGroupSchedulerFactory = new SimpleStateGroupSchedulerFactory(stateGroupSchedulerFactoryConfig);

        EventPublishSchedulerFactory eventPublishSchedulerFactory = new SimpleEventPublishSchedulerFactory();

        SchedulerManagerConfig schedulerManagerConfig = SchedulerManagerConfig.newBuilder().eventPublisherSchedulerSize(1)
                .stateGroupSchedulerCoreSize(1).stateGroupSchedulerSize(2).build();

        SchedulerManager<StateTest.PlayerCmdPair> schedulerManager = new SimpleSchedulerManager<>(stateGroupPool, stateGroupSchedulerFactory, eventPublishSchedulerFactory, schedulerManagerConfig);
        String groupId = "111";
        String[] names = {"1", "2", "3", "4"};
        Random random = new Random();
        final String[] cmds = {ROCK_CMD, PAPER_CMD, SCISSORS_CMD};

        CyclicBarrier cyclicBarrier = new CyclicBarrier(names.length, () -> {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(names.length);
        for (String name : names) {
            executorService.submit(() -> {
                StateTest.PlayerCmdPair playerCmdPair = new StateTest.PlayerCmdPair();
                playerCmdPair.name = name;
                playerCmdPair.cmd = ENTER_CMD;
                schedulerManager.addEvent(groupId, playerCmdPair);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }


                for (int i = 0; i < 100; i++) {
                    String cmd = cmds[random.nextInt(cmds.length)];
                    StateTest.PlayerCmdPair pc = new StateTest.PlayerCmdPair();
                    pc.name = name;
                    pc.cmd = cmd;
                    schedulerManager.addEvent(groupId, pc);
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
