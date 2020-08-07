package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.custom.TaskArchive;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id600_CompletedAppeal extends Command {

    private List<TaskArchive> completed_tasks;
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
                tasks = factory.getTaskDao().getAllTasks(1,chatId);
//                completed_tasks = factory.getTaskArchiveDao().getAllCompletedTasksArchive();
                list = new ArrayList<>();
                tasks.forEach(e -> list.add(String.valueOf(e.getId())));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(getText(525), buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOICE_APPEAL;
                return COMEBACK;
            case CHOICE_APPEAL:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()){
                    task = tasks.get(Integer.parseInt(updateMessageText));
                    StringBuilder sb = new StringBuilder();
                    sb.append(task.getId()).append(next);
                    sb.append(task.getTaskText()).append(next);
                    sb.append(task.getPeopleName()).append(next);
                    sb.append(task.getDateBegin()).append(next);
                    sb.append(userDao.getUserByChatId(task.getEmployeeId()).getFullName()).append(next);
                    sendMessage(sb.toString());
                }
                return EXIT;
        }
        return EXIT;
    }


}
