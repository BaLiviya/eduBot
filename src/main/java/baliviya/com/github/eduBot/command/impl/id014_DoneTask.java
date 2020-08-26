package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.custom.TaskArchive;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id014_DoneTask extends Command {

    private Task task;
    private boolean statusTask;
    private int deleteMessageId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                // кнопка выполнена buttonId 20
                if(isButton(20)){
                    String text        = update.getCallbackQuery().getMessage().getText();
                    int id             = Integer.parseInt(text.split(next)[0].replaceAll("[^0-9]",""));
                    task               = factory.getTaskDao().get(id);
                    statusTask         = true;
                    //toDeleteKeyboard(sendMessage(14));
                    deleteMessageId    = sendMessage(14);
                    waitingType        = WaitingType.DONE;
                   // return COMEBACK;
                }
                return COMEBACK;
            case DONE:
                deleteMessage (updateMessageId);
                deleteMessage(deleteMessageId);
                if(hasMessageText()){
                    taskArchiveDao.insert(updateMessageText, task.getId(),getText(536));//messageId 536 - выполнено
                    closeTask();
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void closeTask() throws TelegramApiException {
        //статус 1 - выполнено
        factory.getTaskDao().updateStatus(task.getId(),1);
        task                            = factory.getTaskDao().get(task.getId());
        List<TaskArchive> taskArchive   = taskArchiveDao.getTasksArchive(task.getId());
        String employeeName             = "";
        for(EmployeeCategory employee : factory.getEmployeeCategoryDao().getByCategoryId(task.getCategoryId())){
            employeeName += userDao.getUserByChatId(employee.getEmployeeChatId()).getFullName() + next;
        }

        StringBuilder sb                = new StringBuilder();
        sb.append("Обращение #").append(task.getId()).append(next);
        if(taskArchive.size() != 0){
            for(TaskArchive taskA : taskArchive){
                sb.append(taskA.getTaskStatus()).append(next);
                sb.append(taskA.getText()).append(next);
            }
        }
        sb.append("<b>Ответственный : ").append(employeeName).append("</b>\n");
        sendMessage(sb.toString(), task.getPeopleId());
        //keyboard id 1 - главное меню
        sendMessageWithKeyboard("Ответ отправлен",1);

    }

}
