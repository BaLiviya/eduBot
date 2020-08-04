package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.CitizensInfo;
import baliviya.com.github.eduBot.entity.custom.CitizensRegistration;
import baliviya.com.github.eduBot.entity.custom.Reception;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id005_Citizens extends Command {

    private List<String> list;
    private List<Reception> receptions;
    private ButtonsLeaf buttonsLeaf;
    private CitizensRegistration citizensRegistration;
    private Reception reception;

    @Override
    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        switch (waitingType) {
            case START:
                //CitizensInfo citizensInfo = factory.getCitizensInfoDao().get();
                //if (citizensInfo.getDocument() != null) bot.execute(new SendDocument().setDocument(citizensInfo.getDocument()).setChatId(chatId));
                receptions = factory.getReceptionDao().getAll();
                list = new ArrayList<>();
                receptions.forEach(e -> list.add(e.getName()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(getText(523), buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOICE_RECEPTION;
                return COMEBACK;
            case CHOICE_RECEPTION:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    reception = receptions.get(Integer.parseInt(updateMessageText));
                    citizensRegistration = new CitizensRegistration();
                    citizensRegistration.setChatId(chatId);
                    citizensRegistration.setReceptionId(receptions.get(Integer.parseInt(updateMessageText)).getId());
                    citizensRegistration.setStatus("Записан");
                    citizensRegistration.setDate(factory.getCitizensInfoDao().getById(reception.getId()) == null ? null : factory.getCitizensInfoDao().getById(reception.getId()).getDate());
                    citizensRegistration.setCitizensTime(factory.getCitizensInfoDao().getById(reception.getId()) == null ? null : factory.getCitizensInfoDao().getById(reception.getId()).getTime());
                    getFullName();
                    waitingType = WaitingType.SET_FULL_NAME;
                }
                return COMEBACK;
            case SET_FULL_NAME:
                deleteMessage(updateMessageId);
                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() <= 50) {
                    User user = new User();
                    user.setFullName(update.getMessage().getText());
                    citizensRegistration.setFullName(update.getMessage().getText());
                    getQuestion();
                    waitingType = WaitingType.SET_QUESTION;
                } else {
                    wrongData();
                    getFullName();
                }
                return COMEBACK;
            case SET_QUESTION:
                deleteMessage(updateMessageId);
                if (hasMessageText()) {
                    citizensRegistration.setQuestion(updateMessageText);
                    toDeleteKeyboard(sendMessageWithKeyboard("<i><b>Подтвердите</b></i>",504));
                    waitingType  = WaitingType.WAIT;
                }
                return COMEBACK;

            case WAIT:
                deleteMessage(updateMessageId);
                if (isButton(524)) {
                    citizensRegistration.setDate(new Date());
                    factory.getCitizensRegistrationDao().insert(citizensRegistration);
                    sendMessage(String.format(getText(524),userDao.getUserByChatId(chatId).getFullName(),reception.getName()));
                    return EXIT;
                }
            return COMEBACK;
        }
        return EXIT;
    }

    private int getQuestion() throws TelegramApiException { return botUtils.sendMessage(Const.SET_QUESTION,chatId);    }

    private int wrongData() throws TelegramApiException{ return botUtils.sendMessage(Const.WRONG_DATA_TEXT,chatId); }

    private int getFullName() throws TelegramApiException { return botUtils.sendMessage(Const.SET_FULL_NAME_MESSAGE,chatId); }

}
