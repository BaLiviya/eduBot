package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.custom.TaskArchive;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id602_InProcessAppeal extends Command {


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
            tasks = factory.getTaskDao().getAllTasks(3, chatId);
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


//                String text = String.format(getText(Const.MESSAGE_IN_PROCESS),task.getId(),task.getTaskText(),task.getPeopleName(),task.getDateBegin(),sb.toString());
//                sendMessageWithKeyboard(text, Const.BACK_BUTTON_IN_MENU);
                sendMessageToEmployee(task.getPeopleName(), task.getId(), sb);
                waitingType = WaitingType.BACK_BUTTON_IN_MENU;

            }

            return COMEBACK;
            case BACK_BUTTON_IN_MENU:
                /*if(isButton(20)){
                    sendMessageWithKeyboard(getText(Const.MESSAGE_IN_PROCESS),buttonsLeaf.getListButton());
                }
                if(isButton(21)){
                    sendMessageWithKeyboard(getText(Const.MESSAGE_IN_PROCESS),buttonsLeaf.getListButton());
                }*/
                deleteMessage(updateMessageId);
                tasks = factory.getTaskDao().getAllTasks(3, chatId);
                list = new ArrayList<>();
                tasks.forEach(e -> list.add(e.getPeopleName()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.MESSAGE_CHOOSE_APPEAL), buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOICE_APPEAL;
                return COMEBACK;
    }
        return EXIT;
 }


    private void        sendMessageToEmployee(String name, int taskId, StringBuilder employeeName) {
        StringBuilder messageToEmployee = new StringBuilder();
        ReplyKeyboard select;
        select = keyboardMarkUpDao.select(Const.KEYBOARD_SEND_MESSAGE_TO_EMPLOYEE_IN_MENU);
        messageToEmployee.append("<b>Обращение # </b>" + taskId).append(next);
        messageToEmployee.append("<b>Заявитель : </b>").append(name).append(next);
        messageToEmployee.append("<b>Текст обращения :</b>").append(task.getTaskText()).append(next);
        messageToEmployee.append("<b>Дата обращения : </b>").append(DateUtil.getDayDate(task.getDateBegin())).append(next);
        messageToEmployee.append("<b>Ответственный : </b>").append(employeeName).append(next);

        for (EmployeeCategory employee : employeeCategoryDao.getByCategoryId(task.getCategoryId())) {
            long directId               = employee.getEmployeeChatId();
            try {
                sendMessageWithKeyboard(messageToEmployee.toString(),select, directId);
            } catch (TelegramApiException e) { e.printStackTrace(); }
        }
    }

}
