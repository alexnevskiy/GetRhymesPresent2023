package com.poly.getrhymespresent2023.bot.handler;

import com.poly.getrhymespresent2023.entities.UserEntity;
import com.poly.getrhymespresent2023.repositories.PresentRepository;
import com.poly.getrhymespresent2023.repositories.QuestionRepository;
import com.poly.getrhymespresent2023.repositories.UserRepository;
import com.poly.getrhymespresent2023.utils.State;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CALLBACK_QUIZ_BEGIN;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_GET_PRESENT;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_QUIZ;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.PARTY_POPPER_EMOJI;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.QUIZ_BEGIN;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createInlineKeyboardMarkup;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createMessageTemplate;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createStickerTemplate;

@Component
@RequiredArgsConstructor
public class AuthHandler implements Handler {

    private final static String congratulationsStickerId =
        "CAACAgIAAxkBAALFFGTc69tM84rMelwekGxssTR7alSaAAJeEgAC7JkpSXzv2aVH92Q7MAQ";

    @Value("${bot.present}")
    private String present;

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PresentRepository presentRepository;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserEntity user, String message, Integer messageId) {
        if (message.equals(COMMAND_QUIZ)) {
            return startQuiz(user);
        } else if (message.equals(COMMAND_GET_PRESENT)) {
            return checkPresent(user);
        }
        var warningMessage = createMessageTemplate(user);
        warningMessage.setText(String.format("Неверная команда\\.\nНажми на кнопку *\"%s\"*, чтобы пройти викторину, " +
                "или же на *\"%s\"* для получения подарка, если ты её уже успешно закончил\\.",
            COMMAND_QUIZ, COMMAND_GET_PRESENT));
        return List.of(warningMessage);
    }

    @Override
    public State operatedBotState() {
        return State.AUTH;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(COMMAND_QUIZ, COMMAND_GET_PRESENT);
    }

    private List<PartialBotApiMethod<? extends Serializable>> startQuiz(UserEntity user) {
        var questionsCount = questionRepository.count();
        var quizMessage = createMessageTemplate(user);
        quizMessage.setText(String.format("Викторина состоит из *%d вопросов*, ответы на которые знает только настоящий " +
            "_GetRhymes_\\. Ограничения по времени нет, поэтому хорошенько подумай над ответами\\.%n%n" +
            "По завершении викторины будет либо *выдан подарок*, либо выведен *список с результатами*, если были допущены " +
            "ошибки\\. _GetRhymes_ конечно та ещё машина, но даже ему свойственно ошибаться\\.%n%n" +
            "Как будешь готов, нажми на кнопку под сообщением\\.", questionsCount));
        var inlineKeyboardMarkup = createInlineKeyboardMarkup(List.of(QUIZ_BEGIN), List.of(CALLBACK_QUIZ_BEGIN));
        quizMessage.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(quizMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> checkPresent(UserEntity user) {
        if (presentRepository.findByUserId(user.getId()).isPresent()) {
            var congratulationsMessage = createMessageTemplate(user);
            var formattedPresent = present.replaceAll("-", "\\\\-");
            congratulationsMessage.setText(String.format("Вот твой подарок от очень занятого человека на 23\\-летие %s:" +
                "%n%n||%s||%n%nАктивируй этот ключ в Steam :\\)", PARTY_POPPER_EMOJI, formattedPresent));
            var congratulationsSticker = createStickerTemplate(user, congratulationsStickerId);
            return List.of(congratulationsMessage, congratulationsSticker);
        }
        var warningMessage = createMessageTemplate(user);
        warningMessage.setText("Ты ещё не прошёл викторину, поэтому подарок пока получить нельзя\\.");
        return List.of(warningMessage);
    }
}
