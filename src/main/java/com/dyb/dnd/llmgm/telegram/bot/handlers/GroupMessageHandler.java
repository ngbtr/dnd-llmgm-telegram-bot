package com.dyb.dnd.llmgm.telegram.bot.handlers;

import com.dyb.dnd.llmgm.telegram.bot.GameMasterInteractionService;
import com.dyb.dnd.llmgm.telegram.bot.character.UserCharacterStorage;
import com.dyb.dnd.llmgm.telegram.bot.model.GameCharacter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public class GroupMessageHandler implements Handler {
    private final GameMasterInteractionService gmInteractionService;
    private final UserCharacterStorage userCharacterStorage;

    public GroupMessageHandler(GameMasterInteractionService gmInteractionService, UserCharacterStorage userCharacterStorage) {
        this.gmInteractionService = gmInteractionService;
        this.userCharacterStorage = userCharacterStorage;
    }

    public Optional<SendMessage> handle(Message message, long userId, String chatId, String text) {
        GameCharacter character = userCharacterStorage.getCharacter(userId);

        if(character != null) {
            String username = message.getFrom().getUserName();

            String response = gmInteractionService.processPlayerMessage(userId, username, text);

            SendMessage reply = new SendMessage(String.valueOf(chatId), response);
            reply.setReplyToMessageId(message.getMessageId());

            // You would need access to bot instance here or a dispatcher
            // bot.execute(reply);

            return Optional.of(reply);
        } else {
            return Optional.empty();
        }
    }
}
