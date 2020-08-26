package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Departments;
import baliviya.com.github.eduBot.entity.custom.DepartmentsInfo;
import baliviya.com.github.eduBot.entity.custom.Staff;
import baliviya.com.github.eduBot.entity.custom.StaffInfo;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class  id021_EditorSpecial extends Command{

    private List<Staff>            staffList;
    private List<StaffInfo>        staffInfosList;
    private List<Departments>      departmentsList;
    private List<DepartmentsInfo>  departmentsInfoList;
    private List<String>           nameList, depNamesList, staffDepNameList;
    private StaffInfo              staffInfo;
    private Departments            department;
    private DepartmentsInfo        departmentsInfo;
    private ButtonsLeaf            staffButtonsLeaf, depButtonsLeaf, staffDepButtonsLeaf;
    private int                    photoMessageId, edit , deleteMessageId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()){
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
                    deleteMessageId = (sendMessageWithKeyboard(staffInfo.getText(), 10));
                    edit = bot.execute(new SendMessage().setText(getText(32 )).setChatId(chatId)).getMessageId();
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
                deleteMessageId = (sendMessageWithKeyboard(departmentsInfo.getText(),10));
                edit = bot.execute(new SendMessage().setText(getText(32)).setChatId(chatId)).getMessageId();
                waitingType = WaitingType.DEP_BACK_BUTTON;
                return COMEBACK;

            case STAFF_UPDATE_PHOTO:
                staffEditPhoto(updateMessagePhoto);
                waitingType = WaitingType.STAFF_BACK_BUTTON;
                return COMEBACK;

            case STAFF_UPDATE_TEXT:
                staffEditText(updateMessageText);
                waitingType = WaitingType.STAFF_BACK_BUTTON;
                return COMEBACK;

            case DEP_UPDATE_PHOTO:
                departmentsEditPhoto(updateMessagePhoto);
                waitingType = WaitingType.DEP_BACK_BUTTON;
                return COMEBACK;

            case DEP_UPDATE_TEXT:
                departmentsEditText(updateMessageText);
                waitingType = WaitingType.DEP_BACK_BUTTON;
                return COMEBACK;

            case STAFF_BACK_BUTTON:

                if (updateMessageText.equals("/editPhoto")) {
                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    toDeleteMessage(sendMessage(30));
                    waitingType = WaitingType.STAFF_UPDATE_PHOTO;
                    return COMEBACK;
                } else if (updateMessageText.equals("/editText")) {
                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    toDeleteMessage(sendMessage(31));
                    waitingType = WaitingType.STAFF_UPDATE_TEXT;
                    return COMEBACK;
                } else if (isButton(1004)) {
                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    btnBack(nameList, staffButtonsLeaf, Const.STRUCTURE_OF_UO);
                    waitingType = WaitingType.CHOOSE_STAFF;
                    return COMEBACK;
                }

            case DEP_BACK_BUTTON:
                if (updateMessageText.equals("/editPhoto")) {
                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    toDeleteMessage(sendMessage(30));
                    waitingType = WaitingType.DEP_UPDATE_PHOTO;
                    return COMEBACK;
                } else if (updateMessageText.equals("/editText")) {
                    deleteMessage(deleteMessageId);
                    deleteMessage(updateMessageId);
                    deleteMessage(photoMessageId);
                    deleteMessage(edit);
                    toDeleteMessage(sendMessage(31));
                    waitingType = WaitingType.DEP_UPDATE_TEXT;
                    return COMEBACK;
                } else if (isButton(1004)) {
                    deleteMessage(deleteMessageId);
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

    private void staffEditPhoto(String photo) throws TelegramApiException {
        deleteMessage(updateMessageId);
        staffInfo.setPhoto(photo);
        staffInfoDao.staffUpdatePhoto(staffInfo);
        staffMenu(updateMessageId, staffInfo);
    }

    private void staffEditText(String text) throws TelegramApiException {
        deleteMessage(updateMessageId);
        staffInfo.setText(text);
        staffInfoDao.staffUpdateText(staffInfo);
        staffMenu(updateMessageId, staffInfo);
    }

    private void departmentsEditPhoto(String photo) throws TelegramApiException {
        deleteMessage(updateMessageId);
        departmentsInfo.setPhoto(photo);
        departmentsInfoDao.depUpdatePhoto(departmentsInfo);
        depMenu(updateMessageId, departmentsInfo);
    }

    private void departmentsEditText(String text) throws TelegramApiException {
        deleteMessage(updateMessageId);
        departmentsInfo.setText(text);
        departmentsInfoDao.depUpdateText(departmentsInfo);
        depMenu(updateMessageId, departmentsInfo);
    }


    private Boolean depMenu(int i, DepartmentsInfo departmentsInfo) throws TelegramApiException {
        deleteMessage(i);
        photoMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(departmentsInfo.getPhoto())).getMessageId();
        deleteMessageId = (sendMessageWithKeyboard(departmentsInfo.getText(),10));
        edit = bot.execute(new SendMessage().setText(getText(32)).setChatId(chatId)).getMessageId();
        return EXIT;
    }

    private Boolean staffMenu(int i, StaffInfo staffInfo) throws TelegramApiException {
        deleteMessage(i);
        photoMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(staffInfo.getPhoto())).getMessageId();
        deleteMessageId = sendMessageWithKeyboard(staffInfo.getText(),10);
        edit = bot.execute(new SendMessage().setText(getText(32)).setChatId(chatId)).getMessageId();
        return EXIT;
    }

}