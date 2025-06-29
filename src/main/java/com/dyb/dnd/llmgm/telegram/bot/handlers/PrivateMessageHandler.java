package com.dyb.dnd.llmgm.telegram.bot.handlers;

import com.dyb.dnd.llmgm.telegram.bot.character.HeroForge;
import com.dyb.dnd.llmgm.telegram.bot.character.UserCharacterStorage;
import com.dyb.dnd.llmgm.telegram.bot.model.GameCharacter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Optional;

public class PrivateMessageHandler implements Handler {
    private final UserCharacterStorage userCharacterStorage;


    public PrivateMessageHandler(UserCharacterStorage userCharacterStorage) {
        this.userCharacterStorage = userCharacterStorage;
    }

    public Optional<SendMessage> handle(Message message, long userId, String chatId, String text) {
        SendMessage replyMessage;
        if (text.equalsIgnoreCase("создать персонажа")) {
            if (HeroForge.isCreating(userId)) {
                replyMessage = new SendMessage(chatId, "Ты уже создаёшь персонажа. Продолжай:");
            } else {
                HeroForge.startCreation(userId);
                replyMessage = new SendMessage(chatId, "Начнём создание персонажа! Как тебя зовут?");
            }
        } else if (HeroForge.isCreating(userId)) {
            String reply = HeroForge.handleStep(userId, text);
            replyMessage = new SendMessage(chatId, reply);
        } else {
            if(!userCharacterStorage.hasCharacter(userId)) {
                // Отправка кнопки "Создать персонажа"
                replyMessage = new SendMessage(chatId, "Привет! Хочешь создать персонажа?");
                ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
                keyboard.setResizeKeyboard(true);
                KeyboardRow row = new KeyboardRow();
                row.add("Создать персонажа");
                keyboard.setKeyboard(List.of(row));
                replyMessage.setReplyMarkup(keyboard);
            } else {
                GameCharacter character = userCharacterStorage.getCharacter(userId);

                if (character != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(character);
                        replyMessage = new SendMessage(chatId, "Твой аватар уже выбран, вот он:\n```json\n" + json + "\n```");
                        replyMessage.setParseMode("Markdown");
                        System.out.println("Sent character JSON to user " + userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        replyMessage = new SendMessage(chatId, "Не удалось сериализовать персонажа.");
                    }
                } else {
                    replyMessage = new SendMessage(chatId, "Не удалось загрузить персонажа.");
                }
            }
        }

        return Optional.of(replyMessage);
    }
}
