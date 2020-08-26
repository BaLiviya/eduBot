package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Category;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id019_AddCategoryForEmployee extends Command {

    private List<User>              userList;
    private List<String>            namesList;
    private List<Category>          categoryList;
    private List<EmployeeCategory>  employeeCategoryList;
    private ButtonsLeaf             buttonsLeaf;
    private User                    employee;
    private Category                category;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if(!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                userList = userDao.getAllByEmployeeChatId();
                namesList     = new ArrayList<>();
                userList.forEach(e -> namesList.add(e.getFullName()));
                buttonsLeaf = new ButtonsLeaf(namesList);
                sendEmployees();
                waitingType = WaitingType.CHOOSE_EMPLOYEE;
                return COMEBACK;

            case CHOOSE_EMPLOYEE:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()){
                    if(buttonsLeaf.isNext(updateMessageText)){
                        sendEmployees();
                        return COMEBACK;
                    }
                    categoryList     = factory.getCategoryDao().getAll();
                    employee         = userList.get(Integer.parseInt(updateMessageText));
                    namesList.clear();
                    categoryList.forEach((e) -> namesList.add(e.getName()));
//                    namesList.add("Назад");
                    buttonsLeaf      = new ButtonsLeaf(namesList);
                    sendViewEmployee();
                    waitingType = WaitingType.CHOICE_CATEGORY;
                }
                return COMEBACK;

            case CHOICE_CATEGORY:
//                if (namesList.get(Integer.parseInt(updateMessageText)).equals("Назад")){
//                    deleteMessage(updateMessageId);
//                    btnBack(namesList, buttonsLeaf, Const.EMPLOYEE);
//                    waitingType = WaitingType.CHOOSE_EMPLOYEE;
//                    return COMEBACK;
//                }
                deleteMessage(updateMessageId);
                if(hasCallbackQuery()) {
                    if (buttonsLeaf.isNext(updateMessageText)){
                        sendViewEmployee();
                        return COMEBACK;
                    }
                    category       = categoryList.get(Integer.parseInt(updateMessageText));
                    boolean isExist = false;
                    for(EmployeeCategory employeeCategory : employeeCategoryList){
                        if(employeeCategory.getCategoryId() == category.getId()){
                            factory.getEmployeeCategoryDao().delete(employeeCategory);
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        EmployeeCategory employeeCategory = new EmployeeCategory();
                        employeeCategory.setCategoryId(category.getId());
                        employeeCategory.setEmployeeChatId(employee.getChatId());
                        factory.getEmployeeCategoryDao().insert(employeeCategory);
                    }
                    sendViewEmployee();
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void sendViewEmployee() throws TelegramApiException {
        deleteMessage(updateMessageId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ответственный: ").append("<b>" + employee.getFullName() + "</b>");
        employeeCategoryList        = factory.getEmployeeCategoryDao().getAll(employee.getChatId());
        stringBuilder.append(next).append("Ответственен за: ");
        for(EmployeeCategory employeeCategory : employeeCategoryList){
            for (Category category : categoryList){
                if(category.getId() == employeeCategory.getCategoryId()) {
                    stringBuilder.append(category.getName()).append(",");
                    break;
                }
            }
        }

        stringBuilder.append(next).append("Назначить/снять (категории: ").append(employeeCategoryList.size()).append(")");
        toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(), buttonsLeaf.getListButton()));

    }

    private int   sendEmployees()  throws TelegramApiException {
        deleteMessage(updateMessageId);
        int messageId = sendMessageWithKeyboard("Сотрудников: " + userList.size() + next + "Выберите сотрудника",
                buttonsLeaf.getListButton());
        toDeleteKeyboard(messageId);
        return messageId;
    }

//    public void btnBack(List<String> list, ButtonsLeaf buttonsLeaf, int a) throws TelegramApiException {
//        buttonsLeaf = new ButtonsLeaf(list);
//        toDeleteKeyboard(sendMessageWithKeyboard(a, buttonsLeaf.getListButton()));
//    }

}
