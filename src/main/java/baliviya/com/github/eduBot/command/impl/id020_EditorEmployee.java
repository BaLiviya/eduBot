package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Category;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;
import baliviya.com.github.eduBot.entity.enums.Language;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id020_EditorEmployee extends Command {

    private List<Category> categoryList;
    private List<EmployeeCategory> employees;
    private Category category;
    private Category newCategoryRu, newCategoryKz;
    private int deleteMessageId;
    private Language currentLanguage = Language.ru;
//    private Category currentId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {

        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }

        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendListCategory();
                waitingType = WaitingType.CHOOSE_CATEGORY;
                return COMEBACK;

            case CHOOSE_CATEGORY:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    if (isCommand("/em")) {
                        category = categoryList.get(getInt());
                        sendInfo();
                        waitingType = WaitingType.EDITION;
                    } else if (isCommand("/new")) {
                        deleteMessageId = sendMessage("Введите название для новой категорий");
                        waitingType = WaitingType.NEW_CATEGORY;
                        return COMEBACK;
                    } else if (isCommand("/st")) {
                        category = categoryList.get(getInt());
                        sendCategoryInfo();
                        waitingType = WaitingType.EDITION;
                    }
                }
                return COMEBACK;

            case EDITION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    if (isCommand("/back")) {
                        sendListCategory();
                        waitingType = WaitingType.CHOOSE_CATEGORY;
                    } else if (isCommand("/del")) {
                        int numberEmployee = Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
                        employeeCategoryDao.delete(employees.get(numberEmployee).getEmployeeChatId(), category.getId());
                        sendInfo();
                        waitingType = WaitingType.EDITION;
                    } else if (isCommand("/edit")) {
                        deleteMessageId = sendMessage("Введите название для категорий");
                        waitingType = WaitingType.UPDATE_CATEGORY;
                    } else if (isCommand("/drop")) {
                        categoryDao.delete(category.getId());
                        sendListCategory();
                        waitingType = WaitingType.CHOOSE_CATEGORY;
                        return COMEBACK;
                    } else if(isCommand("/swap")){
                        changeLanguage();
                    }
                    return COMEBACK;
                }
                if (hasContact()) {
                    registerNewEmployee();
                    return COMEBACK;
                }

            case NEW_CATEGORY:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    newCategoryRu = new Category();
                    newCategoryRu.setName(updateMessageText);
                    newCategoryRu.setLangId(1);
                    categoryDao.insertRU(newCategoryRu);
                    sendMessage("Введите название для казахской версии категорий");
                    newCategoryKz = new Category();
                    newCategoryRu = categoryDao.getTextById(newCategoryRu);
                    waitingType = WaitingType.SET_TEXT_KZ;
                    return COMEBACK;
                }

            case SET_TEXT_KZ:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    newCategoryKz.setLangId(2);
                    newCategoryKz.setName(updateMessageText);
                    newCategoryKz.setId(newCategoryRu.getId());
                    categoryDao.insertKZ(newCategoryKz);
                    sendListCategory();
                    waitingType = WaitingType.CHOOSE_CATEGORY;
                    return COMEBACK;
                }


            case UPDATE_CATEGORY:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    category.setName(updateMessageText);
                    categoryDao.updateByLangId(category);
                    sendCategoryInfo();
                    waitingType = WaitingType.EDITION;
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void sendListCategory() throws TelegramApiException {
        String formatMessage = getText(25);
        StringBuilder infoByEmployee = new StringBuilder();
        categoryList = categoryDao.getAll();
        String format = getText(26);
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            infoByEmployee.append(String.format(format, "/em" + i, "/st" + i, category.getName())).append(next);
        }
        deleteMessageId = sendMessage(String.format(formatMessage, infoByEmployee.toString(), "/new"));
    }

    private void sendInfo() throws TelegramApiException {
        String formatMessage = getText(27);
        StringBuilder employeeList = new StringBuilder();
        employees = employeeCategoryDao.getAll(category.getId());
        int count = 0;
        if (employees != null) {
            for (EmployeeCategory employeeCategory : employees) {
                employeeList.append("/del").append(count).append("X").append(" - \uD83D\uDD0E").append(getLinkForUser(employeeCategory.getEmployeeChatId(), userDao.getUserByChatId(employeeCategory.getEmployeeChatId()).getUserName())).append(next);
                count++;
            }
        }
        String result = String.format(formatMessage, category.getName() + "",
                employeeList.toString(), "Чтобы добавить нового ответственного, отправьте контакт пользователя. Он должен быть зарегистрированным.",
                "/back");
        deleteMessageId = sendMessage(result);
    }

    private void sendCategoryInfo() throws TelegramApiException {
        String formatMessage = getText(28);
        String languageInfo = null;
        if (category.getLangId() == 1) {
            languageInfo = "\uD83C\uDDF7\uD83C\uDDFA ru";
        } else if (category.getLangId() == 2) {
            languageInfo = "\uD83C\uDDF0\uD83C\uDDFF kz";
        }
        deleteMessageId = sendMessage(String.format(formatMessage, category.getName(), languageInfo, "/swap","/edit", "/drop", "/back"));
    }

    private boolean registerNewEmployee() throws TelegramApiException {
        long newEmployeeChatId = update.getMessage().getContact().getUserID();
        if (!userDao.isRegistered(newEmployeeChatId)) {
            sendMessage(Const.USER_DO_NOT_REGISTERED);
            sendInfo();
            waitingType = WaitingType.EDITION;
            return COMEBACK;
        } else {
            if (employeeCategoryDao.isEmployee(category.getId(), newEmployeeChatId)) {
                sendMessage("Пользователь уже ответственный");
            } else {
                employeeCategoryDao.addEmployee(category.getId(), newEmployeeChatId);
            }
            sendInfo();
            waitingType = WaitingType.EDITION;
        }
        return COMEBACK;
    }

    private boolean isCommand(String command) {
        return updateMessageText.startsWith(command);
    }

    private int getInt() {
        return Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
    }

    private void changeLanguage() throws TelegramApiException {
        if (currentLanguage == Language.ru){
            currentLanguage = Language.kz;
        } else {
            currentLanguage = Language.ru;
        }
        category = categoryDao.getAllCategoryByLangId(category.getId(), currentLanguage);
        sendCategoryInfo();
        waitingType = WaitingType.EDITION;
    }

}