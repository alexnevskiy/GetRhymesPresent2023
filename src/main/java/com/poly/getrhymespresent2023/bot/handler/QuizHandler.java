package com.poly.getrhymespresent2023.bot.handler;

import com.poly.getrhymespresent2023.entities.PresentEntity;
import com.poly.getrhymespresent2023.entities.SessionEntity;
import com.poly.getrhymespresent2023.entities.UserEntity;
import com.poly.getrhymespresent2023.repositories.PresentRepository;
import com.poly.getrhymespresent2023.repositories.QuestionRepository;
import com.poly.getrhymespresent2023.repositories.SessionRepository;
import com.poly.getrhymespresent2023.repositories.UserRepository;
import com.poly.getrhymespresent2023.utils.State;
import com.poly.getrhymespresent2023.utils.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CALLBACK_CORRECT_ANSWER;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CALLBACK_INCORRECT_ANSWER;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CALLBACK_QUIZ_BEGIN;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CHECK_MARK_EMOJI;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_GET_PRESENT;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_QUIZ;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.COMMAND_QUIZ_END;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CONFETTI_BALL_EMOJI;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.CROSS_MARK_EMOJI;
import static com.poly.getrhymespresent2023.utils.ApplicationConstants.PARTY_POPPER_EMOJI;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createInlineKeyboardMarkup;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createMessageTemplate;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createReplyKeyboard;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createStickerTemplate;

@Component
@RequiredArgsConstructor
public class QuizHandler implements Handler {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final SessionRepository sessionRepository;
    private final PresentRepository presentRepository;

    @Value("${bot.present}")
    private String present;

    private final static String congratulationsStickerId =
        "CAACAgIAAxkBAALFEmTc6nE-LQM55aLUsk7l5ktCH6v5AALjCgACoR3wSxeUER5bRv8NMAQ";
    private final static String presentImage = "happy_end.jpg";

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserEntity user, String message, Integer messageId) {
        switch (message) {
            case CALLBACK_QUIZ_BEGIN -> {
                if (user.getState().equals(State.QUIZ)) {
                    return Collections.emptyList();
                }
                user.setState(State.QUIZ);
                userRepository.save(user);
                var quizBeginMessage = createMessageTemplate(user);
                quizBeginMessage.setText("Викторина начинается\\!");
                var replyKeyboardMarkup = createReplyKeyboard(List.of(COMMAND_QUIZ_END));
                quizBeginMessage.setReplyMarkup(replyKeyboardMarkup);
                return Stream.concat(Stream.of(quizBeginMessage), getNextQuestion(user).stream()).toList();
            }
            case COMMAND_QUIZ_END -> {
                user.setState(State.AUTH);
                userRepository.save(user);
                sessionRepository.deleteByUser(user);
                var quizEndMessage = createMessageTemplate(user);
                var replyKeyboardMarkup = createReplyKeyboard(List.of(COMMAND_QUIZ, COMMAND_GET_PRESENT));
                quizEndMessage.setText("Ты решил закончить викторину :\\(\n\nЕсли захочешь снова её пройти, то нажми на " +
                    "соответствующую кнопку\\.");
                quizEndMessage.setReplyMarkup(replyKeyboardMarkup);
                return List.of(quizEndMessage);
            }
            case CALLBACK_CORRECT_ANSWER -> {
                var userAnswers = sessionRepository.findByUser(user);
                var questionNumber = userAnswers == null ? 1 : userAnswers.size() + 1;
                var sessionAnswer = new SessionEntity();
                sessionAnswer.setUser(user);
                sessionAnswer.setQuestion(questionRepository.findById(questionNumber).get());
                sessionAnswer.setCorrect(true);
                sessionRepository.save(sessionAnswer);
                return getNextQuestion(user);
            }
            case CALLBACK_INCORRECT_ANSWER -> {
                var userAnswers = sessionRepository.findByUser(user);
                var questionNumber = userAnswers == null ? 1 : userAnswers.size() + 1;
                var sessionAnswer = new SessionEntity();
                sessionAnswer.setUser(user);
                sessionAnswer.setQuestion(questionRepository.findById(questionNumber).get());
                sessionAnswer.setCorrect(false);
                sessionRepository.save(sessionAnswer);
                return getNextQuestion(user);
            }
        }
        var warningMessage = createMessageTemplate(user);
        warningMessage.setText("Неверная команда\\.\nНажми на кнопку с вариантом ответа, чтобы получить " +
                "следующий вопрос викторины\\.");
        return List.of(warningMessage);
    }

    @Override
    public State operatedBotState() {
        return State.QUIZ;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(CALLBACK_QUIZ_BEGIN, COMMAND_QUIZ_END, CALLBACK_CORRECT_ANSWER, CALLBACK_INCORRECT_ANSWER);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getNextQuestion(UserEntity user) {
        var userAnswers = sessionRepository.findByUser(user);
        var questionNumber = userAnswers == null ? 1 : userAnswers.size() + 1;
        var question = questionRepository.findById(questionNumber);
        if (question.isEmpty()) {
            sessionRepository.deleteByUser(user);
            assert userAnswers != null;
            return getQuizResults(user, userAnswers);
        }

        var answers = new ArrayList<>(List.of(question.get().getAnswer1(), question.get().getAnswer2(),
            question.get().getAnswer3(), question.get().getCorrectAnswer()));
        Collections.shuffle(answers);
        var callbackDataList = new ArrayList<String>();
        for (String answer : answers) {
            var callbackData = answer.equals(question.get().getCorrectAnswer()) ? CALLBACK_CORRECT_ANSWER :
                CALLBACK_INCORRECT_ANSWER;
            callbackDataList.add(callbackData);
        }

        if (question.get().getImageName() != null && question.get().getImageName().endsWith("gif")) {
            var questionMessage = TelegramUtil.createDocumentTemplate(user, question.get().getImageName());
            questionMessage.setCaption(String.format("_*Вопрос №%d*_%n%n%s", questionNumber, question.get().getQuestion()));
            var inlineKeyboardMarkup = createInlineKeyboardMarkup(answers, callbackDataList);
            questionMessage.setReplyMarkup(inlineKeyboardMarkup);
            return List.of(questionMessage);
        } else if (question.get().getImageName() != null) {
            var questionMessage = TelegramUtil.createPhotoTemplate(user, question.get().getImageName());
            questionMessage.setCaption(String.format("_*Вопрос №%d*_%n%n%s", questionNumber, question.get().getQuestion()));
            var inlineKeyboardMarkup = createInlineKeyboardMarkup(answers, callbackDataList);
            questionMessage.setReplyMarkup(inlineKeyboardMarkup);
            return List.of(questionMessage);
        }

        var questionMessage = createMessageTemplate(user);
        var formattedQuestion = question.get().getQuestion();
        questionMessage.setText(String.format("_*Вопрос №%d*_%n%n%s", questionNumber, formattedQuestion));
        var inlineKeyboardMarkup = createInlineKeyboardMarkup(answers, callbackDataList);
        questionMessage.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(questionMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getQuizResults(UserEntity user,
                                                                             List<SessionEntity> userAnswers) {
        user.setState(State.AUTH);
        userRepository.save(user);
        var isAllCorrect = userAnswers.stream().allMatch(SessionEntity::isCorrect);
        var replyKeyboardMarkup = createReplyKeyboard(List.of(COMMAND_QUIZ, COMMAND_GET_PRESENT));
        if (isAllCorrect) {
            presentRepository.findByUserId(user.getId()).orElseGet(() -> {
                var present = new PresentEntity();
                present.setUser(user);
                return presentRepository.save(present);
            });

            var congratulationsMessage = TelegramUtil.createPhotoTemplate(user, presentImage);
            var formattedPresent = present.replaceAll("-", "\\\\-");
            congratulationsMessage.setCaption(String.format("Ты ответил на все вопросы верно, значит перед нами настоящий " +
                "Никита Тарасенко aka _GetRhymes_\\!%n%nОчень занятой человек поздравляет тебя от всей души с " +
                "Днём Рождения, желает множества счастливых моментов в жизни, крепкого здоровья, огромного багажа удачи, " +
                "много\\-много любви, море денег, верных друзей и достижения всех целей\\! Пусть судьба преподносит " +
                "тебе только приятные подарки и сюрпризы, а программистские навыки стремятся к Long\\.MAX\\_VALUE\\!%s%s%n%n" +
                "Вот твой подарок от очень занятого человека на 23\\-летие %s:" +
                "%n%n||%s||%n%nАктивируй этот ключ в Steam :\\)%n%nP\\.S\\. На фото ты и очень занятой человек",
                PARTY_POPPER_EMOJI, CONFETTI_BALL_EMOJI, PARTY_POPPER_EMOJI, formattedPresent));
            congratulationsMessage.setReplyMarkup(replyKeyboardMarkup);
            var congratulationsSticker = createStickerTemplate(user, congratulationsStickerId);
            return List.of(congratulationsMessage, congratulationsSticker);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_*Результаты викторины:*_\n\n");
        for (int i = 0; i < userAnswers.size(); i++) {
            var result = userAnswers.get(i).isCorrect() ? CHECK_MARK_EMOJI : CROSS_MARK_EMOJI;
            stringBuilder.append(String.format("Вопрос %d: %s%n", i + 1, result));
        }
        stringBuilder.append("\nПройди заново викторину и ответь на все вопросы верно, чтобы получить свой подарок\\!");
        var resultsMessage = createMessageTemplate(user);
        resultsMessage.setText(stringBuilder.toString());
        resultsMessage.setReplyMarkup(replyKeyboardMarkup);
        return List.of(resultsMessage);
    }
}
