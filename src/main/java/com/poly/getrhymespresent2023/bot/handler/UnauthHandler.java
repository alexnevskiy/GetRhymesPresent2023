package com.poly.getrhymespresent2023.bot.handler;

import com.poly.getrhymespresent2023.entities.UserEntity;
import com.poly.getrhymespresent2023.utils.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.poly.getrhymespresent2023.utils.TelegramUtil.createMessageTemplate;
import static com.poly.getrhymespresent2023.utils.TelegramUtil.createStickerTemplate;

@Component
public class UnauthHandler implements Handler {

    private final Random random = new Random();
    private final List<String> stickerIds = List.of(
        "CAACAgIAAxkBAALCumTbvbjW9Y_IgRmUoVshpKSvWtqyAAImGQACcLGJSMYDjZ5pqtSxMAQ",
        "CAACAgIAAxkBAALCx2TbxeqLDOJKNVOEUN00ZcPWSFY3AALiGQAC3NiJSG9u7ivWju9-MAQ",
        "CAACAgIAAxkBAALC0WTbxlz0If5B8LxeZZU_sl4mmAOpAAIyGwACvv6JSH1gNvieRggyMAQ",
        "CAACAgIAAxkBAALC1WTbxmoTK_vOpx_7D6YHAuXJ_F-WAAL5GwACc5KJSCTOdmH2d5atMAQ",
        "CAACAgIAAxkBAALC12TbxnjsrEbkbZD4orfiOQwPjVvzAAJaFQACHNSJSHpVX7H4rNJDMAQ",
        "CAACAgIAAxkBAALC2mTbxovazQRSmMX-Ag9Y9tWjqKl4AAL6GQAC1JmASARF2qiXRyYlMAQ",
        "CAACAgIAAxkBAALC3GTbxphHLlO5x0OpA1LYQ6Zi2dZUAAKuGQACKPGJSMQitKvDimMaMAQ",
        "CAACAgIAAxkBAALC3mTbxsur004DbU_xA7WM5ZgK3vlnAAK7GQAC-X2JSBGi6zB-iIyDMAQ"
    );

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserEntity user, String message, Integer messageId) {
        var sendMessage = createMessageTemplate(user);
        sendMessage.setText("Ты пытаешься выдать себя за _GetRhymes_\\!");
        var sendSticker = createStickerTemplate(user, getRandomStickerId());
        return List.of(sendMessage, sendSticker);
    }

    @Override
    public State operatedBotState() {
        return State.UNAUTH;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }

    private String getRandomStickerId() {
        return stickerIds.get(random.nextInt(stickerIds.size()));
    }
}
