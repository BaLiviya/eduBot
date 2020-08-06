package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Category;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import baliviya.com.github.eduBot.util.components.IKeyboardOld;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id004_Task extends Command {

    private List<String>            categoryName = new ArrayList<>();
    private List<Category>          categories;
    private List<EmployeeCategory>  employeeCategories;
    private ButtonsLeaf             buttonsLeaf;
    private int                     categoryId;
    private Task                    task;
    private int                     deleteMessageId;
    private int                     taskId;

    @Override
    public boolean execute() throws TelegramApiException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                task        = new Task();
                task.setStatusId(3);
                task.setDateBegin(new Date());
                task.setPeopleId(chatId);

                task.setPeopleName(userDao.getUserByChatId(chatId).getFullName());
                categories  = categoryDao.getAll();
                categories.forEach(category -> categoryName.add(category.getName()));
                buttonsLeaf = new ButtonsLeaf(categoryName);
                sendMessageWithKeyboard(getText(Const.CHOICE_CATEGORY_MESSAGE), buttonsLeaf.getListButton());
                waitingType = WaitingType.CHOICE_CATEGORY;
                return COMEBACK;
            case CHOICE_CATEGORY:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    categoryId          = categories.get(Integer.parseInt(updateMessageText)).getId();
                    task.setCategoryId(categoryId);
                    employeeCategories  = employeeCategoryDao.getByCategoryId(categoryId);
                    deleteMessageId     = sendMessage(Const.SEND_TEXT_HANDLING_MESSAGE);
                    waitingType         = WaitingType.HANDLING_TEXT;
                }
                return COMEBACK;
            case HANDLING_TEXT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    task.setTaskText(updateMessageText);
                    task.setMessageId(update.getMessage().getMessageId());
                    taskId = taskDao.insert(task);
                    StringBuilder employeeName  = new StringBuilder();
                    employeeCategoryDao.getByCategoryId(task.getCategoryId()).forEach(suggestion -> employeeName.append(userDao.getUserByChatId(suggestion.getEmployeeChatId()).getFullName()).append(next));

                    sendMessageToUser(taskId, employeeName);
                    sendMessageToEmployee(task.getPeopleName(), taskId, employeeName);
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void        sendMessageToEmployee(String name, int taskId, StringBuilder employeeName) {
        StringBuilder messageToEmployee = new StringBuilder();
        ReplyKeyboard select;
        select = keyboardMarkUpDao.select(7);
//        messageToEmployee.append(getLinkT("Обращение # " + taskId)).append(next);
        messageToEmployee.append("<b>Обращение # </b>" + taskId).append(next);
        messageToEmployee.append("<b>Заявитель : </b>").append(name).append(next);
        messageToEmployee.append("<b>Текст обращения :</b>").append(task.getTaskText()).append(next);
        messageToEmployee.append("<b>Дата обращения : </b>").append(DateUtil.getDayDate(task.getDateBegin())).append(next);
        messageToEmployee.append("<b>Ответственный : </b>").append(employeeName).append(next);

        for (EmployeeCategory employee : employeeCategoryDao.getByCategoryId(task.getCategoryId())) {
            long directId               = employee.getEmployeeChatId();
          IKeyboardOld kb             = new IKeyboardOld();
          kb.next();
//            List<SuggestionFile> filesList  = factory.getSuggestionFileDao().getFilesList(taskId);
//            for (SuggestionFile doc : filesList) {
//                try {
//                    if (doc.getImg() != null) {
//                        bot.execute(new SendPhoto().setChatId(directId).setPhoto(doc.getImg()));
//                    } else if (doc.getDoc() != null) {
//                        bot.execute(new SendDocument().setChatId(directId).setDocument(doc.getDoc()));
//                    } else if (doc.getAudio() != null) {
//                        bot.execute(new SendVoice().setChatId(directId).setVoice(doc.getAudio()));
//                    } else if (doc.getVideo() != null) {
//                        bot.execute(new SendVideo().setChatId(directId).setVideo(doc.getVideo()));
//                    }
//                } catch (TelegramApiException e) { e.printStackTrace(); }
//            }
            try {
                sendMessageWithKeyboard(messageToEmployee.toString(), select, directId);
            } catch (TelegramApiException e) { e.printStackTrace(); }
        }
//        ReplyKeyboard select;
    }

    private void    sendMessageToUser(int taskId, StringBuilder employeeName)     throws TelegramApiException {
        deleteMessage(updateMessageId);
        sendMessage(String.format(getText(Const.HANDLING_ACCEPT_MESAGE), task.getPeopleName(), taskId, employeeName));
//        toDeleteKeyboard(sendMessageWithKeyboard(String.format(getText(1004), peopleName, taskId, employeeName), 1));
    }
}
