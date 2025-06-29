package com.dyb.dnd.llmgm.telegram.bot;

import com.dyb.dnd.llmgm.telegram.bot.character.HeroForge;
import com.dyb.dnd.llmgm.telegram.bot.character.UserCharacterStorage;
import com.dyb.dnd.llmgm.telegram.bot.handlers.GroupMessageHandler;
import com.dyb.dnd.llmgm.telegram.bot.handlers.PrivateMessageHandler;
import com.dyb.dnd.llmgm.telegram.bot.model.GameCharacter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

public class TavernScribeBot extends TelegramLongPollingBot {
    //todo mapper container? + IoC maybew in future
    private static ObjectMapper mapper = new ObjectMapper();

    private UserCharacterStorage userCharacterStorage;
    private GameMasterInteractionService gmInteractionService;

    public TavernScribeBot(UserCharacterStorage userCharacterStorage, GameMasterInteractionService gmInteractionService) {
        this.userCharacterStorage = userCharacterStorage;
        this.gmInteractionService = gmInteractionService;
    }

    @Override
    public String getBotUsername() {
        return "TavernScribeBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("TAVERN_SCRIBE_BOT_TG_API_KEY");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText().trim();

        Message message = update.getMessage();

        Optional<SendMessage> replyMessage = message.getChat().isUserChat()
                ? new PrivateMessageHandler(userCharacterStorage).handle(message, userId, chatId, text)
                : new GroupMessageHandler(gmInteractionService, userCharacterStorage).handle(message, userId, chatId, text);

        replyMessage.ifPresent(it -> {
            try {
                execute(it);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });

    }
}
