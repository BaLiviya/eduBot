package baliviya.com.github.eduBot.config;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.PropertiesDao;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private Map<Long, Conversation> conversations = new HashMap<>();
    private DaoFactory              daoFactory    = DaoFactory.getInstance();
    private PropertiesDao           propertiesDao = daoFactory.getPropertiesDao();
    private String                  tokenBot;
    private String                  nameBot;

    @Override
    public void             onUpdateReceived(Update update)  {
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (TelegramApiException | IOException | SQLException e) {
            log.error("Error №" + e);
        }
    }

    private Conversation    getConversation(Update update) {
        Long chatId                 = UpdateUtil.getChatId(update);
        Conversation conversation   = conversations.get(chatId);
        if (conversation == null) {
            log.info("InitNormal new conversation for '{}'", chatId);
            conversation            = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    @Override
    public String           getBotUsername() {
        if (nameBot == null || nameBot.isEmpty()) nameBot = propertiesDao.getPropertiesValue(Const.BOT_NAME);
        return nameBot;
    }

    @Override
    public String           getBotToken() {
        if (tokenBot == null || tokenBot.isEmpty()) tokenBot = propertiesDao.getPropertiesValue(Const.BOT_TOKEN);
        return tokenBot;
    }
}
