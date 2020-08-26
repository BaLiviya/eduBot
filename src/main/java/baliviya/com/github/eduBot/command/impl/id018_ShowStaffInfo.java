package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.*;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id018_ShowStaffInfo extends Command {

    private List<Staff> staffList;
    private List<StaffInfo> staffInfosList;
    private List<Departments> departmentsList;
    private List<DepartmentsInfo> departmentsInfoList;
    private List<String> nameList, depNamesList, staffDepNameList;
    private StaffInfo staffInfo;
    private Departments department;
    private DepartmentsInfo departmentsInfo;
    private ButtonsLeaf staffButtonsLeaf, depButtonsLeaf, staffDepButtonsLeaf;
    private int photoMessageId, edit, deleteMessageId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }

        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                staffList = staffDao.getAll();
                nameList = new ArrayList<>();
                staffList.forEach(e -> nameList.add(e.getName()));
                staffButtonsLeaf = new ButtonsLeaf(nameList);
                toDeleteKeyboard(sendMessageWithKeyboard(Const.STRUCTURE_OF_UO, staffButtonsLeaf.getListButton()));
                waitingType = WaitingType.CHOOSE_STAFF;
                return COMEBACK;

            case CHOOSE_STAFF:
                if (Integer.parseInt(updateMessageText) != 4) {
                    deleteMessage(updateMessageId);
                    staffInfo = staffInfoDao.getInfo(staffList.get(Integer.parseInt(updateMessageText)).getId());
                    photoMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(staffInfo.getPhoto())).getMessageId();
                    toDeleteKeyboard(sendMessageWithKeyboard(staffInfo.getText(), 10));
                    waitingType = WaitingType.STAFF_BACK_BUTTON;
                    return COMEBACK;

                } else {

                    deleteMessage(updateMessageId);
                    departmentsList = departmentsDao.getAll();
                    depNamesList = new ArrayList<>();
                    departmentsList.forEach((e) -> depNamesList.add(e.getName()));
                    depNamesList.add("Назад");
                    depButtonsLeaf = new ButtonsLeaf(depNamesList);
                    toDeleteKeyboard(sendMessageWithKeyboard(Const.DEPARTMENTS_OF_UO, depButtonsLeaf.getListButton()));
                    waitingType = WaitingType.CHOOSE_DEPARTMENT;
                    return COMEBACK;
                }

            case CHOOSE_DEPARTMENT:
                if (depNamesList.get(Integer.parseInt(updateMessageText)).equals("Назад")) {
                    deleteMessage(updateMessageId);
                    btnBack(nameList, staffButtonsLeaf, Const.DEPARTMENTS_OF_UO);
                    waitingType = WaitingType.CHOOSE_STAFF;
                    return COMEBACK;
                }
                deleteMessage(updateMessageId);
                departmentsInfoList = departmentsInfoDao.getDepartmentInfo(departmentsList.get(Integer.parseInt(updateMessageText)).getId());
                staffDepNameList = new ArrayList<>();
                departmentsInfoList.forEach((e) -> staffDepNameList.add(e.getName()));
                staffDepNameList.add("Назад");
                staffDepButtonsLeaf = new ButtonsLeaf(staffDepNameList);
                toDeleteKeyboard(sendMessageWithKeyboard(Const.DEPARTMENT_TITLE, staffDepButtonsLeaf.getListButton()));
                waitingType = WaitingType.CHOOSE_STAFF_OF_DEPARTMENT;
                return COMEBACK;

            case CHOOSE_STAFF_OF_DEPARTMENT:
                if (staffDepNameList.get(Integer.parseInt(updateMessageText)).equals("Назад")) {
                    deleteMessage(updateMessageId);
                    btnBack(depNamesList, depButtonsLeaf, Const.DEPARTMENT_TITLE);
                    waitingType = WaitingType.CHOOSE_DEPARTMENT;
                    return COMEBACK;
                }
                deleteMessage(updateMessageId);
                department = departmentsDao.getDepartmentsById(departmentsInfoList.get(Integer.parseInt(updateMessageText)).getDepartmentsId());
                departmentsInfo = departmentsInfoDao.getInfo(departmentsInfoList.get(Integer.parseInt(updateMessageText)).getPositionId(), department.getId());
                photoMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(departmentsInfo.getPhoto())).getMessageId();
                toDeleteKeyboard(sendMessageWithKeyboard(departmentsInfo.getText(), 10));
                waitingType = WaitingType.DEP_BACK_BUTTON;
                return COMEBACK;


            case STAFF_BACK_BUTTON:
                if (isButton(1004)) {
//                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    btnBack(nameList, staffButtonsLeaf, Const.STRUCTURE_OF_UO);
                    waitingType = WaitingType.CHOOSE_STAFF;
                    return COMEBACK;
                }

            case DEP_BACK_BUTTON:
                if (isButton(1004)) {
//                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    btnBack(staffDepNameList, staffDepButtonsLeaf, Const.DEPARTMENT_TITLE);
                    waitingType = WaitingType.CHOOSE_STAFF_OF_DEPARTMENT;
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    public void btnBack(List<String> list, ButtonsLeaf buttonsLeaf, int a) throws TelegramApiException {
        buttonsLeaf = new ButtonsLeaf(list);
        toDeleteKeyboard(sendMessageWithKeyboard(a, buttonsLeaf.getListButton()));
    }
}