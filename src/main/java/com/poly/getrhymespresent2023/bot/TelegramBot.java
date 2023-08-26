package com.poly.getrhymespresent2023.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    @Getter
    private String botName;
    @Value("${bot.token}")
    @Getter
    private String botToken;

    private final UpdateReceiver updateReceiver;

    @Override
    public void onUpdateReceived(Update update) {
        var messagesToSend = updateReceiver.handle(update);
        if (messagesToSend != null && !messagesToSend.isEmpty()) {
            messagesToSend.forEach(message -> {
                if (message instanceof SendMessage) {
                    sendAnswerMessage((SendMessage) message);
                }
                if (message instanceof SendDocument) {
                    sendAnswerDocument((SendDocument) message);
                }
                if (message instanceof SendPhoto) {
                    sendAnswerPhoto((SendPhoto) message);
                }
                if (message instanceof SendSticker) {
                    sendAnswerSticker((SendSticker) message);
                }
            });
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendAnswerDocument(SendDocument message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendAnswerPhoto(SendPhoto message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendAnswerSticker(SendSticker message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
