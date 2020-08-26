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

public class id015_NotCompletedTask extends Command {

    private Task task;
    private boolean statusTask;
    private int deleteMessageId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                if (isButton(21)) {
                    String text = update.getCallbackQuery().getMessage().getText();
                    int id = Integer.parseInt(text.split(next)[0].replaceAll("[^0-9]", ""));
                    task = factory.getTaskDao().get(id);
                    statusTask = true;
                    deleteMessageId = sendMessage(14);
                    waitingType = WaitingType.NOT_COMPLETED;
                    return COMEBACK;
                }
                return COMEBACK;
            case NOT_COMPLETED:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    taskArchiveDao.insert(updateMessageText, task.getId(), getText(537)); // messageId 537 - Не выполнено
                    closeTask();
                    return COMEBACK;
                }
        }
        return EXIT;

    }

    private void closeTask() throws TelegramApiException {
        factory.getTaskDao().updateStatus(task.getId(),2);
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
                sb.append(taskA.getText()).append(next);
            }
        }
        sb.append("<b>Статус : ").append("</b>");
        if (taskArchive.size() != 0) {
            for (TaskArchive taskArchive1 : taskArchive){
                sb.append(taskArchive1.getTaskStatus()).append(next);
            }
        }
        sb.append("<b>Ответственный : ").append(employeeName).append("</b>\n");
        sendMessage(sb.toString(), task.getPeopleId());
        sendMessageWithKeyboard("Ответ отправлен",1);

    }

}
