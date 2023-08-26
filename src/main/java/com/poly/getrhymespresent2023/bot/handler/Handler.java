package com.poly.getrhymespresent2023.bot.handler;

import com.poly.getrhymespresent2023.entities.UserEntity;
import com.poly.getrhymespresent2023.utils.State;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {

    List<PartialBotApiMethod<? extends Serializable>> handle(UserEntity user, String message, Integer messageId);

    State operatedBotState();

    List<String> operatedCallBackQuery();
}
