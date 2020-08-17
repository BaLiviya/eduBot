package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.dao.impl.StaffDao;
import baliviya.com.github.eduBot.entity.custom.Departments;
import baliviya.com.github.eduBot.entity.custom.Position;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.Staff;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id018_ShowStaffInfo extends Command {

    private List<Position> positions;
    private List<Departments> departments;
    private List<Staff> staffs;
    private List<String> list, list01, list02;
    private ButtonsLeaf buttonsLeaf, buttonsLeaf01, buttonsLeaf02;
    private Position position;
    private Staff staff, staffDep;
    private int currentId, deleteMessageId, deleteMessageId01;
//    private Departments department;


    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                positions = factory.getPositionDao().getMenu(1);
                list = new ArrayList<>();
                positions.forEach((e) -> list.add(e.getPosition()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(Const.STRUCTURE_OF_UO, buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOOSE_POSITION;
                return COMEBACK;

            case CHOOSE_POSITION:
                 if (Integer.parseInt(updateMessageText) != 4) {
                    deleteMessage(updateMessageId);
                    staff = staffDao.getStaff(positions.get(Integer.parseInt(updateMessageText)).getId());
                    position = positionDao.getPosition(staff.getPositionId());
                    deleteMessageId = bot.execute(new SendPhoto().setPhoto(staff.getPhoto()).setChatId(chatId)).getMessageId();
                     toDeleteKeyboard(sendMessageWithKeyboard(String.format(getText(Const.STAFF_INFORMATION), staff.getFullName(),
                            position.getPosition(), staff.getPhone(), staff.getMPhone()),9));

                    waitingType = WaitingType.BACK_BUTTON;
                    return COMEBACK;

                } else {
                     deleteMessage(updateMessageId);
                     departments = departmentDao.getAll();
                     list01 = new ArrayList<>();
                     departments.forEach((e) -> list01.add(e.getName()));
                     list01.add("Назад");
                     buttonsLeaf01 = new ButtonsLeaf(list01);
                     toDeleteKeyboard(sendMessageWithKeyboard(Const.DEPARTMENTS_OF_UO, buttonsLeaf01.getListButton()));
                     waitingType = WaitingType.CHOOSE_DEPARTMENT;
                     return COMEBACK;
                 }

            case CHOOSE_DEPARTMENT:
                if (list01.get(Integer.parseInt(updateMessageText)).equals("Назад")){
                    deleteMessage(updateMessageId);
                    btnBack(list, buttonsLeaf, Const.STRUCTURE_OF_UO);
                    waitingType = WaitingType.CHOOSE_POSITION;
                    return COMEBACK;
                }
                    currentId = Integer.parseInt(updateMessageText);
//                list.clear();
                    deleteMessage(updateMessageId);
                    staffs = staffDao.getStaffDepart(departments.get(Integer.parseInt(updateMessageText)).getId());
                    list02 = new ArrayList<>();
                    staffs.forEach((e) -> list02.add(e.getFullName()));
                    list02.add("Назад");
                    buttonsLeaf02 = new ButtonsLeaf(list02);
                    toDeleteKeyboard(sendMessageWithKeyboard(Const.DEPARTMENT_TITLE, buttonsLeaf02.getListButton()));
                    waitingType = waitingType.STAFF_DEPART;
                    return COMEBACK;



            case STAFF_DEPART:
                if (list02.get(Integer.parseInt(updateMessageText)).equals("Назад")){
                    deleteMessage(updateMessageId);
                    btnBack(list01, buttonsLeaf01, Const.DEPARTMENTS_OF_UO);
                    waitingType = WaitingType.CHOOSE_DEPARTMENT;
                    return COMEBACK;
                }
                deleteMessage(updateMessageId);
                List<Staff> staffStaff = new ArrayList<>();
                staffStaff = staffDao.getAll(currentId + 1);
                staffDep = staffStaff.get(Integer.parseInt(updateMessageText));
                deleteMessageId01 = bot.execute(new SendPhoto().setPhoto(staffDep.getPhoto()).setChatId(chatId)).getMessageId();
                toDeleteKeyboard( sendMessageWithKeyboard(String.format(getText(Const.STAFF_INFORMATION02),
                        staffDep.getFullName(), staffDep.getPhone(), staffDep.getMPhone()),9));
                waitingType = WaitingType.BACK_BUTTON01;
                return COMEBACK;


            case BACK_BUTTON:
                deleteMessage(deleteMessageId);
                if (isButton(1004)){
                    deleteMessage(updateMessageId);
                    btnBack(list, buttonsLeaf, Const.STRUCTURE_OF_UO);
                    waitingType = WaitingType.CHOOSE_POSITION;
                    return COMEBACK;
                }

            case BACK_BUTTON01:
                deleteMessage(deleteMessageId01);
                if(isButton(1004)){
                    deleteMessage(updateMessageId);
                    btnBack(list02, buttonsLeaf02, Const.DEPARTMENT_TITLE);
                    waitingType = WaitingType.STAFF_DEPART;
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


