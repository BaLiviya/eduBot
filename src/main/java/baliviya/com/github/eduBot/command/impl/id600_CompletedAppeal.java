package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.custom.TaskArchive;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id600_CompletedAppeal extends Command {

    private List<String> list;
    private ButtonsLeaf buttonsLeaf;
    private TaskArchive taskArchive;
    private Task task;
    private List<Task> tasks;
    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        deleteMessage(updateMessageId);
        switch (waitingType){
            case START:
                tasks = factory.getTaskDao().getAllTasks(1, chatId);
                list = new ArrayList<>();
                tasks.forEach(e -> list.add(e.getPeopleName()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.MESSAGE_CHOOSE_APPEAL), buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOICE_APPEAL;
                return COMEBACK;
            case CHOICE_APPEAL:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()){
                    task = tasks.get(Integer.parseInt(updateMessageText));
                    taskArchive = factory.getTaskArchiveDao().getTaskArchive(task.getId());


                    StringBuilder sb = new StringBuilder();
                    for (EmployeeCategory employeeCategory : factory.getEmployeeCategoryDao().getByCategoryId(task.getCategoryId())){
                        sb.append(userDao.getUserByChatId(employeeCategory.getEmployeeChatId()).getFullName()).append(", ");
                    }

                    String text = String.format(getText(Const.MESSAGE_COMPLETED),task.getId(),task.getTaskText(),task.getPeopleName(),task.getDateBegin(),sb.toString(),taskArchive.getText());
//                    sendMessage(text);
                    sendMessageWithKeyboard(text,Const.BACK_BUTTON_IN_MENU);
                    waitingType = WaitingType.BACK_BUTTON_IN_MENU;

                }
                return COMEBACK;
            case BACK_BUTTON_IN_MENU:
                deleteMessage(updateMessageId);
                tasks = factory.getTaskDao().getAllTasks(1, chatId);
                list = new ArrayList<>();
                tasks.forEach(e -> list.add(e.getPeopleName()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.MESSAGE_CHOOSE_APPEAL), buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOICE_APPEAL;
                return COMEBACK;
        }
        return EXIT;
    }


}
