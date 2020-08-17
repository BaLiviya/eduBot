package baliviya.com.github.eduBot.service;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.MessageDao;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.BotUtil;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class RegistrationService {

    private User            user;
    private long            chatId;
    private BotUtil         botUtil;
    private List<String>    list;
    private ButtonsLeaf     buttonsLeaf;
    private WaitingType     waitingType = WaitingType.CHOOSE_BUTTON;
    private DaoFactory      factory     = DaoFactory.getInstance();
    private MessageDao      messageDao  = factory.getMessageDao();
    private boolean         COMEBACK    = false;
    private boolean         EXIT        = true;

    public  boolean isRegistration(Update update, BotUtil botUtil)  throws TelegramApiException {
        if (botUtil == null || chatId == 0) {
            chatId       = UpdateUtil.getChatId(update);
            this.botUtil = botUtil;
        }
        switch (waitingType) {
            case CHOOSE_BUTTON:
                user        = new User();
                user.setChatId(chatId);
                getName();
                waitingType = WaitingType.SET_FULL_NAME;
                return COMEBACK;
            case SET_FULL_NAME:
                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() <= 50) {
                    user.setFullName(update.getMessage().getText());
                    getPhone();
                    waitingType = WaitingType.SET_PHONE_NUMBER;
                } else {
                    wrongData();
                    getName();
                }
                return COMEBACK;
            case SET_PHONE_NUMBER:
                if (botUtil .hasContactOwner(update)) {
                    user    .setPhone(update.getMessage().getContact().getPhoneNumber());
                    user    .setUserName(UpdateUtil.getFrom(update));
                    return EXIT;
                } else {
                    wrongData();
                    getPhone();
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int     wrongData()                                     throws TelegramApiException { return botUtil.sendMessage(Const.WRONG_DATA_TEXT, chatId); }

    private int     getName()                                       throws TelegramApiException { return botUtil.sendMessage(Const.SET_FULL_NAME_MESSAGE, chatId); }

    private int     getPhone()                                      throws TelegramApiException { return botUtil.sendMessage(Const.SEND_CONTACT_MESSAGE, chatId); }

    public  User    getUser() { return user; }
}
