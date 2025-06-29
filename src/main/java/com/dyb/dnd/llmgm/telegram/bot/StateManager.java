package com.dyb.dnd.llmgm.telegram.bot;

import java.util.concurrent.ConcurrentHashMap;

public class StateManager {
    private static final ConcurrentHashMap<String, String> playerStates = new ConcurrentHashMap<>();

    public static void saveState(String playerId, String state) {
        playerStates.put(playerId, state);
    }

    public static String getState(String playerId) {
        return playerStates.getOrDefault(playerId, "{}");
    }
}
