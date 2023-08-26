package com.poly.getrhymespresent2023.utils;

import com.poly.getrhymespresent2023.entities.UserEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TelegramUtil {

    public static SendMessage createMessageTemplate(UserEntity user) {
        return createMessageTemplate(String.valueOf(user.getTelegramId()));
    }

    public static SendMessage createMessageTemplate(String chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("MarkdownV2");
        return message;
    }

    public static SendDocument createDocumentTemplate(UserEntity user, String documentPath) {
        return createDocumentTemplate(String.valueOf(user.getTelegramId()), documentPath);
    }

    public static SendDocument createDocumentTemplate(String chatId, String documentPath) {
        var document = new SendDocument();
        document.setChatId(chatId);
        document.setParseMode("MarkdownV2");
        document.setDocument(new InputFile().setMedia(new File("images/" + documentPath)));
        return document;
    }

    public static SendPhoto createPhotoTemplate(UserEntity user, String documentPath) {
        return createPhotoTemplate(String.valueOf(user.getTelegramId()), documentPath);
    }

    public static SendPhoto createPhotoTemplate(String chatId, String photoPath) {
        var photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setParseMode("MarkdownV2");
        photo.setPhoto(new InputFile().setMedia(new File("images/" + photoPath)));
        return photo;
    }

    public static SendSticker createStickerTemplate(UserEntity user, String stickerId) {
        return createStickerTemplate(String.valueOf(user.getTelegramId()), stickerId);
    }

    public static SendSticker createStickerTemplate(String chatId, String stickerId) {
        var sticker = new SendSticker();
        sticker.setChatId(chatId);
        var stickerFile = new InputFile();
        stickerFile.setMedia(stickerId);
        sticker.setSticker(stickerFile);
        return sticker;
    }

    public static ReplyKeyboardMarkup createReplyKeyboard(List<String> buttons) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        var keyboardRows = new ArrayList<KeyboardRow>();
        for (String buttonName : buttons) {
            keyboardRows.add(new KeyboardRow(List.of(new KeyboardButton(buttonName))));
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createInlineKeyboardMarkup(List<String> texts, List<String> callbackData) {
        var inlineKeyBoardMarkup = new InlineKeyboardMarkup();
        var keyboard = new ArrayList<List<InlineKeyboardButton>>();
        for (int i = 0; i < texts.size(); i++) {
            var inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(texts.get(i));
            inlineKeyboardButton.setCallbackData(callbackData.get(i));
            keyboard.add(List.of(inlineKeyboardButton));
        }
        inlineKeyBoardMarkup.setKeyboard(keyboard);
        return inlineKeyBoardMarkup;
    }
}
