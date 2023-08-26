package com.poly.getrhymespresent2023.bot;

import com.poly.getrhymespresent2023.bot.handler.Handler;
import com.poly.getrhymespresent2023.entities.UserEntity;
import com.poly.getrhymespresent2023.repositories.UserRepository;
import com.poly.getrhymespresent2023.utils.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateReceiver {

    private final List<Handler> handlers;
    private final UserRepository userRepository;

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        if (isMessageWithText(update)) {
            var message = update.getMessage();
            var user = getOrSaveUser(message.getFrom());
            log.info(String.format("Message from %s with state %s: %s", user.getUsername(),
                user.getState().toString(), message.getText()));
            return getHandlerByState(user.getState()).handle(user, message.getText(), message.getMessageId());
        } else if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var user = getOrSaveUser(callbackQuery.getFrom());
            log.info(String.format("Callback from %s with state %s: %s", user.getUsername(),
                user.getState().toString(), callbackQuery.getData()));
            return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData(),
                callbackQuery.getMessage().getMessageId());
        }
        return Collections.emptyList();
    }

    private UserEntity getOrSaveUser(User telegramUser) {
        return userRepository.findByTelegramId(telegramUser.getId())
            .orElseGet(() -> {
                UserEntity newUser = new UserEntity();
                newUser.setTelegramId(telegramUser.getId());
                newUser.setFirstName(telegramUser.getFirstName());
                newUser.setLastName(telegramUser.getLastName());
                newUser.setUsername(telegramUser.getUserName());
                newUser.setState(State.NONE);
                return userRepository.save(newUser);
            });
    }

    private Handler getHandlerByState(State state) {
        return handlers.stream()
            .filter(handler -> handler.operatedBotState() != null)
            .filter(handler -> handler.operatedBotState().equals(state))
            .findAny()
            .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return handlers.stream()
            .filter(handler -> handler.operatedCallBackQuery().stream()
                .anyMatch(query::startsWith))
            .findAny()
            .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
