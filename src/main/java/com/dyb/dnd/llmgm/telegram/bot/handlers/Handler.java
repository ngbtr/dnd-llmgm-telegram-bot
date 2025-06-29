package com.dyb.dnd.llmgm.telegram.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public interface Handler {
    Optional<SendMessage> handle(Message message, long userId, String chatId, String text);
}
