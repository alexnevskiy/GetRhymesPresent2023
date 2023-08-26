package com.poly.getrhymespresent2023.bot.handler;

import com.poly.getrhymespresent2023.entities.UserEntity;
import com.poly.getrhymespresent2023.repositories.TrustedUserRepository;
import com.poly.getrhymespresent2023.repositories.UserRepository;
import com.poly.getrhymespresent2023.utils.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_GET_PRESENT;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_QUIZ;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_START;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createMessageTemplate;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createReplyKeyboard;

@Component
@RequiredArgsConstructor
public class NoneHandler implements Handler {

    private final UserRepository userRepository;
    private final TrustedUserRepository trustedUserRepository;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserEntity user, String message, Integer messageId) {
        if (message.equals(COMMAND_START)) {
            var welcomeMessage = createMessageTemplate(user);
            welcomeMessage.setText(String.format(
                    "Привет, *%s*\\!%nСначала я должен определить, что это настоящий аккаунт самого жёсткого прогера " +
                        "на планете под ником _GetRhymes_\\.", user.getFirstName()
            ));
            var initMessage = createMessageTemplate(user);
            if (trustedUserRepository.findByTelegramId(user.getTelegramId()) != null) {
                initMessage.setText("Да, действительно, это настоящий аккаунт _GetRhymes_\\. Но настоящий ли это его " +
                    "владелец \\- ещё предстоит узнать\\.\n\nДля получения подарка начни викторину соответствующей кнопкой\\.");
                var replyKeyboardMarkup = createReplyKeyboard(List.of(COMMAND_QUIZ, COMMAND_GET_PRESENT));
                initMessage.setReplyMarkup(replyKeyboardMarkup);
                user.setState(State.AUTH);
            } else {
                initMessage.setText("Это не настоящий аккаунт _GetRhymes_, поэтому получить подарок ты сейчас не можешь\\.");
                user.setState(State.UNAUTH);
            }
            userRepository.save(user);
            return List.of(welcomeMessage, initMessage);
        }
        var warningMessage = createMessageTemplate(user);
        warningMessage.setText("Чтобы начать работу с ботом введи `/start`\\.");
        return List.of(warningMessage);
    }

    @Override
    public State operatedBotState() {
        return State.NONE;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
