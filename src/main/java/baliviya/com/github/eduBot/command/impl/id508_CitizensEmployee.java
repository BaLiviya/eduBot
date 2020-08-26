package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.CitizensEmployee;
import baliviya.com.github.eduBot.entity.custom.CitizensInfo;
import baliviya.com.github.eduBot.entity.custom.Reception;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import baliviya.com.github.eduBot.util.components.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class id508_CitizensEmployee extends Command {
	private int                         deleteMessageId;
	private List<Reception>             receptions;
	private Reception                   reception;
	private List<CitizensEmployee>      employees;
	private CitizensInfo                info;
	private DateKeyboard                dateKeyboard;
	private Reception                   newReception;
	private Date                        start;
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		
		if (!isAdmin()) {
			sendMessage(Const.NO_ACCESS);
			return EXIT;
		}
		
		switch (waitingType){
			case START:
				deleteMessage(updateMessageId);
				sendListCategory();
				waitingType = WaitingType.CHOICE_CATEGORY;
				return COMEBACK;
			case CHOICE_CATEGORY:
				delete();
				if (hasMessageText()) {
					if (isCommand("/em")){
						reception           = receptions.get(getInt());
						sendInfo();
						waitingType = WaitingType.EDITION;
					} else if (isCommand("/st")) {
						reception           = receptions.get(getInt());
						sendCategoryInfo();
						waitingType = WaitingType.EDITION;
						return COMEBACK;
					} else if (isCommand("/new")) {
						deleteMessageId = sendMessage(getText(542));
						waitingType = WaitingType.NEW_CATEGORY;
					}
				}
				return COMEBACK;
			case EDITION:
				delete();
				if (hasMessageText()) {
					if (isCommand("/back")) {
						sendListCategory();
						waitingType = WaitingType.CHOICE_CATEGORY;
					} else if (isCommand("/del")) {
						int numberEmployee = Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
						factory.getCitizensEmployeeDao().delete(employees.get(numberEmployee).getChatId(), reception.getId());
						sendInfo();
					} else if (isCommand("/edit")) {
						deleteMessageId = sendMessage(getText(544));
						waitingType = WaitingType.UPDATE_CATEGORY;
					} else if (isCommand("/drop")) {
						factory.getReceptionDao().delete(reception.getId());
						sendListCategory();
						waitingType = WaitingType.CHOICE_CATEGORY;
					} else if (isCommand("/time")) {
						dateKeyboard    = new DateKeyboard();
						deleteMessageId = sendStartDate();
						waitingType = WaitingType.START_DATE;
					}
				}
				if (hasContact()) registerNewEmployee();
				return COMEBACK;
			case NEW_CATEGORY:
				delete();
				if (hasMessageText()) {
					newReception            = new Reception();
					newReception.setName(updateMessageText);
					factory.getReceptionDao().insert(newReception);
					sendListCategory();
					waitingType = WaitingType.CHOICE_CATEGORY;
				}
				return COMEBACK;
			case UPDATE_CATEGORY:
				delete();
				if (hasMessageText()) {
					reception.setName(updateMessageText);
					factory.getReceptionDao().update(reception);
					sendListCategory();
					waitingType = WaitingType.EDITION;
				}
				return COMEBACK;
			case START_DATE:
				delete();
				if (hasCallbackQuery()) {
					start               = dateKeyboard.getDateDate(updateMessageText);
					deleteMessageId     = sendMessage(getText(547));
					waitingType         = WaitingType.SEND_TIME;
				}
				return COMEBACK;
			case SEND_TIME:
				delete();
				if (hasMessageText()) {
					CitizensInfo updateInfo = new CitizensInfo();
					updateInfo.setId(reception.getId());
					updateInfo.setDate(start);
					updateInfo.setTime(updateMessageText);
					factory.getCitizensInfoDao().update(updateInfo);
					sendCategoryInfo();
					waitingType = WaitingType.EDITION;
				}
				return COMEBACK;
		}
		return EXIT;
	}
	
	private void registerNewEmployee() throws TelegramApiException {
		long newEmpChatId   = updateMessage.getContact().getUserID();
		if (!userDao.isRegistered(newEmpChatId)) {
			sendMessage(Const.USER_DO_NOT_REGISTERED);
			sendInfo();
		} else {
			if (factory.getCitizensEmployeeDao().isCitizenEmployee(newEmpChatId, reception.getId())){
				sendMessage(getText(546));
			} else {
				factory.getCitizensEmployeeDao().addEmployee(newEmpChatId, reception.getId());
			}
			sendInfo();
		}
	}
	
	private int sendStartDate() throws TelegramApiException {
		return toDeleteKeyboard(sendMessageWithKeyboard(getText(545), dateKeyboard.getCalendarKeyboard()));
	}
	
	private void sendCategoryInfo() throws TelegramApiException {
		String formatMessage            = getText(543);
		info                            = factory.getCitizensInfoDao().getById(reception.getId());
		deleteMessageId                 = sendMessage(String.format(formatMessage, reception.getName(), info.getDate() != null && info.getTime() != null
				? DateUtil.getDayDate(info.getDate()) + space + info.getTime() :
				space, "/edit", "/drop", "/time", "/back"));
	}
	
	private void sendInfo() throws TelegramApiException {
		String formatMessage            = getText(540); //messageId 540 - О категорий
		StringBuilder listEmployeeSB    = new StringBuilder();
		employees                       = factory.getCitizensEmployeeDao().getList(reception.getId());
		int count                       = 0;
		if (employees != null) {
			for (CitizensEmployee employee : employees){
				listEmployeeSB.append("/del")
						.append(count)
						.append("❌")
						.append(" - \uD83D\uDD0E ")
						.append(getLinkForUser(employee.getChatId(),userDao.getUserByChatId(employee.getChatId()).getUserName()))
						.append(next);
				count++;
			}
		}
		String result = String.format(formatMessage, reception.getName() + space, listEmployeeSB.toString(),
				getText(541),"/back");
		deleteMessageId = sendMessage(result);
	}
	
	private int getInt() {
		return Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
	}
	
	private boolean isCommand(String command) {
		return updateMessageText.startsWith(command);
	}
	
	private void delete(){
		deleteMessage(updateMessageId);
		deleteMessage(deleteMessageId);
	}
	
	private void sendListCategory() throws TelegramApiException {
		String formatMessage            = getText(538); //messageID 538 - Список категорий
		StringBuilder infoByEmployee    = new StringBuilder();
		receptions                      = factory.getReceptionDao().getAll();
		String format                   = getText(539); //messageID 539 - 👨 %s - ⚙️ %s - 🧾 %s.
		for (int index = 0; index < receptions.size(); index ++){
			Reception reception = receptions.get(index);
			infoByEmployee.append(String.format(format,"/em" + index,"/st" + index, reception.getName())).append(next);
		}
		deleteMessageId                 = sendMessage(String.format(formatMessage, infoByEmployee.toString(), "/new"));
	}
}
