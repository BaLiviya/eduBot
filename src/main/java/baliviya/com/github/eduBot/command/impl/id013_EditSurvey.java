package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Quest;
import baliviya.com.github.eduBot.entity.enums.Language;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public class id013_EditSurvey extends Command {

    private List<Quest> questsList;
    private Quest quest;
    private int deleteMessageId;
    private int questId;
    private boolean isUpdate;
    private String  icon = "\uD83C\uDDF7\uD83C\uDDFA ru";;
    private Language currentLanguage = Language.ru;

    @Override
    public boolean execute() throws TelegramApiException, IOException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendListQuestion();
                waitingType = WaitingType.CHOICE_OPTION;
                return COMEBACK;

            case CHOICE_OPTION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if(updateMessageText.startsWith("/swap")){
                    if(currentLanguage == Language.ru){
                        currentLanguage = Language.kz;
                        icon = "\uD83C\uDDF0\uD83C\uDDFF kz";
                    } else{
                        currentLanguage = Language.ru;
                    }
                    sendListQuestion();
                }
                if (hasMessageText()) {
                    if (updateMessageText.startsWith("/new")) {
                        deleteMessageId = sendMessage("Введите вопрос для опроса");
                        waitingType = WaitingType.SET_TEXT;
                    } else if (updateMessageText.startsWith("/del")) {
                        questId = questsList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""))).getId();
                        questDao.delete(questId);
                        sendListQuestion();
                    } else if (updateMessageText.startsWith("/st")) {
                        questId = questsList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""))).getId();
                        deleteMessageId = sendMessage("Введите вопрос для опроса");
                        waitingType = WaitingType.SET_TEXT;
                        isUpdate = true;
                    }
                }
                return COMEBACK;

            case SET_TEXT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    if (isUpdate) {
                        quest = questDao.getById(questId);
                        quest.setName(updateMessageText);
                        quest.setLangId(currentLanguage.getId());
                        questDao.update(quest);
                    } else {
                        quest = new Quest();
                        quest.setName(updateMessageText);
                        quest.setLangId(1);
                        quest.setId(questDao.insertRu(quest));
                        sendMessage("Введите вопрос для казахского версии");
                        waitingType = WaitingType.SET_TEXT_SURVEY_KZ;
                        return COMEBACK;
                    }
                    sendListQuestion();
                    waitingType = WaitingType.CHOICE_OPTION;
                }
                return COMEBACK;

            case SET_TEXT_SURVEY_KZ:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if(hasMessageText()){
                    quest.setLangId(2);
                    quest.setName(updateMessageText);
                    questDao.insertKz(quest);
                    sendListQuestion();
                    waitingType = WaitingType.CHOICE_OPTION;
                   return COMEBACK;
                }
        }
        return EXIT;
    }

    private void sendListQuestion() throws TelegramApiException {
        String formatMessage = getText(17);
        StringBuilder stringBuilder = new StringBuilder();
        questsList = questDao.getAllByLangId(currentLanguage);
        String format = getText(18);
        for (int indexQuest = 0; indexQuest < questsList.size(); indexQuest++) {
            Quest quest = questsList.get(indexQuest);
            stringBuilder.append(String.format(format, "/del" + indexQuest,
                    "/st" + indexQuest, quest.getName())).append(next);
        }
        deleteMessageId = sendMessage(String.format(formatMessage, icon,  stringBuilder.toString(), "/new"));
    }
}
