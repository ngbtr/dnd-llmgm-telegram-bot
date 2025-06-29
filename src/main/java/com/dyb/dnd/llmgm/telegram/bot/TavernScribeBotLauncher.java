package com.dyb.dnd.llmgm.telegram.bot;

import com.dyb.dnd.llmgm.telegram.bot.character.UserCharacterStorage;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TavernScribeBotLauncher {
    private static UserCharacterStorage userCharacterStorage;
    private static GameMasterInteractionService gmInteractionService;

    public static void main(String[] args) {
        initComponents();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TavernScribeBot(userCharacterStorage, gmInteractionService));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static void initComponents() {
        userCharacterStorage = new UserCharacterStorage();
        userCharacterStorage.init();

        gmInteractionService = new GameMasterInteractionService();
    }
}
