package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Quest;
import baliviya.com.github.eduBot.entity.custom.Survey;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id010_SurveyQuestion extends Command {

    private ArrayList<String> list;
    private ButtonsLeaf buttonsLeaf;
    private int deleteMessageId;
    private Survey survey;
    private List<Quest> allQuestion;
    private List<String> answers = new ArrayList<>();

    @Override
    public boolean execute() throws TelegramApiException {
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                allQuestion = questDao.getAll();
                if (allQuestion == null || allQuestion.size() == 0){
                    sendMessage("Опрос успешно пройден");
                    return EXIT;
                }
                survey = new Survey();
                survey.setChatId(chatId);
                deleteMessageId = setQuestionText(allQuestion.get(0).getName());
                waitingType = WaitingType.QUEST;
                return COMEBACK;
            case QUEST:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if(hasCallbackQuery()){
                    if (allQuestion == null || allQuestion.size() == 0){
                        StringBuilder stringBuilder = new StringBuilder();
                        answers.add(updateMessageText);
                        for(String answer: answers){
                            stringBuilder.append(answer).append(Const.SPLIT);
                        }
                        survey.setAnswers(stringBuilder.toString().substring(0, stringBuilder.length() - 1));
                        survey.setDateAnswer(new Date());
                        factory.getSurveyDao().insert(survey);
                        sendMessage("Опрос успешно пройден");
                        return EXIT;
                    }
                    answers.add(updateMessageText);
                }
                deleteMessageId = setQuestionText(allQuestion.get(0).getName());
                waitingType  = waitingType.QUEST;
                return COMEBACK;
        }
        return EXIT;
    }
    private int setQuestionText(String text) throws TelegramApiException{
        list = new ArrayList<>();
        list.add("Нет");
        list.add("Да");
        buttonsLeaf = new ButtonsLeaf(list);
        allQuestion.remove(0);
//        return toDeleteKeyboard(sendMessageWithKeyboard(text,buttonsLeaf.getListButton()));
        return toDeleteKeyboard(sendMessageWithKeyboard(text, buttonsLeaf.getListButton()));
    }
}
