package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.*;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import baliviya.com.github.eduBot.util.components.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id019_AddCategoryForEmployee extends Command {

    private List<User>              userList;
    private List<String>            namesList;
    private List<Category>          categoryList;
    private List<EmployeeCategory>  employeeCategoryList;
    private ButtonsLeaf             buttonsLeaf;
    private User                    employee;
    private Category                category;
    private boolean isExist;
    private int deleteMessageId;
    private List<Category> categories;
    private List<EmployeeCategory> employees;
    private DateKeyboard dateKeyboard;
    private Category newCategory;
    private Date start;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if(!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                sendListCategory();
                waitingType = WaitingType.CHOICE_CATEGORY;
//                userList = userDao.getAllByEmployeeChatId();
//                namesList     = new ArrayList<>();
//                userList.forEach(e -> namesList.add(e.getFullName()));
//                buttonsLeaf = new ButtonsLeaf(namesList);
//                sendEmployees();
//                waitingType = WaitingType.CHOOSE_EMPLOYEE;
                return COMEBACK;

            case CHOICE_CATEGORY:
                delete();
                if (hasMessageText()) {
                    if (isCommand("/em")) {
                        category = categories.get(getInt());
                        sendInfo();
                        waitingType = WaitingType.EDITION;
                    } else if (isCommand("/st")) {
                        category = categories.get(getInt());
                        sendCategoryInfo();
                        waitingType = WaitingType.EDITION;
                        return COMEBACK;
                    } else if (isCommand("/new")) {
                        deleteMessageId = sendMessage(getText(542));
                        waitingType = WaitingType.NEW_CATEGORY;
                    }
                }
//                if (hasCallbackQuery()){
//                    if(buttonsLeaf.isNext(updateMessageText)){
//                        sendEmployees();
//                        return COMEBACK;
//                    }
//                    categoryList     = factory.getCategoryDao().getAll();
//                    employee         = userList.get(Integer.parseInt(updateMessageText));
//                    namesList.clear();
//                    categoryList.forEach((e) -> namesList.add(e.getName()));
////                    namesList.add("Назад");
//                    buttonsLeaf      = new ButtonsLeaf(namesList);
//                    sendViewEmployee();
//                    waitingType = WaitingType.CHOICE_CATEGORY;
//                }
                return COMEBACK;

            case EDITION:
                delete();
                if (hasMessageText()) {
                    if (isCommand("/back")) {
                        sendListCategory();
                        waitingType = WaitingType.CHOICE_CATEGORY;
                    } else if (isCommand("/del")) {
                        int numberEmployee = Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
                        factory.getEmployeeCategoryDao().delete(employees.get(numberEmployee).getEmployeeChatId(), category.getId());
                        sendInfo();
                    } else if (isCommand("/edit")) {
                        deleteMessageId = sendMessage(getText(544));
                        waitingType = WaitingType.UPDATE_CATEGORY;
                    } else if (isCommand("/drop")) {
                        factory.getCategoryDao().delete(category.getId());
                        sendListCategory();
                        waitingType = WaitingType.CHOICE_CATEGORY;
                    }
                }
                if (hasContact()) registerNewEmployee();
//                if (namesList.get(Integer.parseInt(updateMessageText)).equals("Назад")){
//                    deleteMessage(updateMessageId);
//                    btnBack(namesList, buttonsLeaf, Const.EMPLOYEE);
//                    waitingType = WaitingType.CHOOSE_EMPLOYEE;
//                    return COMEBACK;
//                }
//                deleteMessage(updateMessageId);
//                if(hasCallbackQuery()) {
//                    if (buttonsLeaf.isNext(updateMessageText)){
//                        sendViewEmployee();
//                        return COMEBACK;
//                    }
//                    category       = categoryList.get(Integer.parseInt(updateMessageText));
//                    isExist = false;
//                    for(EmployeeCategory employeeCategory : employeeCategoryList){
//                        if(employeeCategory.getCategoryId() == category.getId()){
//                            factory.getEmployeeCategoryDao().delete(employeeCategory);
//                            isExist = true;
//                        }
//                    }
//                    if (!isExist) {
//                        EmployeeCategory employeeCategory = new EmployeeCategory();
//                        employeeCategory.setCategoryId(category.getId());
//                        employeeCategory.setEmployeeChatId(employee.getChatId());
//                        factory.getEmployeeCategoryDao().insert(employeeCategory);
//                    }
//                    sendViewEmployee();
//                }
                return COMEBACK;
            case NEW_CATEGORY:
                delete();
                if (hasMessageText()) {
                    newCategory            = new Category();
                    newCategory.setName(updateMessageText);
                    factory.getCategoryDao().insert(newCategory);
                    sendListCategory();
                    waitingType = WaitingType.CHOICE_CATEGORY;
                }
                return COMEBACK;
            case UPDATE_CATEGORY:
                delete();
                if (hasMessageText()) {
                    category.setName(updateMessageText);
                    factory.getCategoryDao().update(category);
                    sendListCategory();
                    waitingType = WaitingType.EDITION;
                }
                return COMEBACK;
//            case START_DATE:
//                delete();
//                if (hasCallbackQuery()) {
//                    start               = dateKeyboard.getDateDate(updateMessageText);
//                    deleteMessageId     = sendMessage(getText(547));
//                    waitingType         = WaitingType.SEND_TIME;
//                }
//                return COMEBACK;
//            case SEND_TIME:
//                delete();
//                if (hasMessageText()) {
//                    CitizensInfo updateInfo = new CitizensInfo();
//                    updateInfo.setId(category.getId());
//                    updateInfo.setDate(start);
//                    updateInfo.setTime(updateMessageText);
//                    factory.getCitizensInfoDao().update(updateInfo);
//                    sendCategoryInfo();
//                    waitingType = WaitingType.EDITION;
//                }
//                return COMEBACK;
        }
        return EXIT;
    }

    private void registerNewEmployee() throws TelegramApiException {
        long newEmpChatId   = updateMessage.getContact().getUserID();
        if (!userDao.isRegistered(newEmpChatId)) {
            sendMessage(Const.USER_DO_NOT_REGISTERED);
            sendInfo();
        } else {
            if (factory.getEmployeeCategoryDao().isEmployee(newEmpChatId, category.getId())){
                sendMessage(getText(546));
            } else {
                factory.getEmployeeCategoryDao().addEmployee(category.getId(),newEmpChatId);
            }
            sendInfo();
        }
    }

    private int sendStartDate() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(545), dateKeyboard.getCalendarKeyboard()));
    }

    private void sendListCategory() throws TelegramApiException {
        String formatMessage            = getText(538); //messageID 538 - Список категорий
        StringBuilder infoByEmployee    = new StringBuilder();
        categories                      = factory.getCategoryDao().getAll();
        String format                   = getText(539); //messageID 539 - 👨 %s - ⚙️ %s - 🧾 %s.
        for (int index = 0; index < categories.size(); index ++){
            Category category = categories.get(index);
            infoByEmployee.append(String.format(format,"/em" + index,"/st" + index, category.getName())).append(next);
        }
        deleteMessageId                 = sendMessage(String.format(formatMessage, infoByEmployee.toString(), "/new"));
    }

    private boolean isCommand(String command) {
        return updateMessageText.startsWith(command);
    }

    private void delete(){
        deleteMessage(updateMessageId);
        deleteMessage(deleteMessageId);
    }

    private int getInt() {
        return Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
    }

    private void sendInfo() throws TelegramApiException {
        String formatMessage            = getText(540); //messageId 540 - О категорий
        StringBuilder listEmployeeSB    = new StringBuilder();
        employees                       = factory.getEmployeeCategoryDao().getList(category.getId());
        int count                       = 0;
        if (employees != null) {
            for (EmployeeCategory employee : employees){
                listEmployeeSB.append("/del")
                        .append(count)
                        .append("❌")
                        .append(" - \uD83D\uDD0E ")
                        .append(getLinkForUser(employee.getEmployeeChatId(),userDao.getUserByChatId(employee.getEmployeeChatId()).getUserName()))
                        .append(next);
                count++;
            }
        }
        String result = String.format(formatMessage, category.getName() + space, listEmployeeSB.toString(),
                getText(541),"/back");
        deleteMessageId = sendMessage(result);
    }

    private void sendCategoryInfo() throws TelegramApiException {
        String formatMessage            = getText(548);
        deleteMessageId                 = sendMessage(String.format(formatMessage, category.getName(),
                space, "/edit", "/drop", "/back"));
    }

//    private void sendViewEmployee() throws TelegramApiException {
//        deleteMessage(updateMessageId);
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("Ответственный: ").append("<b>" + employee.getFullName() + "</b>");
//        employeeCategoryList        = factory.getEmployeeCategoryDao().getAll(employee.getChatId());
//        stringBuilder.append(next).append("Ответственен за: ");
//        for(EmployeeCategory employeeCategory : employeeCategoryList){
//            for (Category category : categoryList){
//                if(category.getId() == employeeCategory.getCategoryId()) {
//                    stringBuilder.append(category.getName()).append(",");
//                    break;
//                }
//            }
//        }
//
//        stringBuilder.append(next).append("Назначить/снять (категории: ").append(employeeCategoryList.size()).append(")");
//        toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(), buttonsLeaf.getListButton()));
//
//    }
//
//    private int   sendEmployees()  throws TelegramApiException {
//        deleteMessage(updateMessageId);
//        int messageId = sendMessageWithKeyboard("Сотрудников: " + userList.size() + next + "Выберите сотрудника",
//                buttonsLeaf.getListButton());
//        toDeleteKeyboard(messageId);
//        return messageId;
//    }

//    public void btnBack(List<String> list, ButtonsLeaf buttonsLeaf, int a) throws TelegramApiException {
//        buttonsLeaf = new ButtonsLeaf(list);
//        toDeleteKeyboard(sendMessageWithKeyboard(a, buttonsLeaf.getListButton()));
//    }

}
