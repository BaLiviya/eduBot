package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.service.TaskReportService;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.components.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id505_TaskReport extends Command {
	private int deleteMessageId;
	private List<String> list;
	private ButtonsLeaf buttonsLeaf;
	private String categoryType;
	private int categoryId;
	private DateKeyboard dateKeyboard;
	private Date start;
	private Date end;
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		
		if (!isAdmin()) {
			sendMessage(Const.NO_ACCESS);
			return EXIT;
		}
		
		switch (waitingType){
			case START:
				deleteMessage(updateMessageId);
				deleteMessageId = getCategoryType();
				waitingType = WaitingType.SET_CATEGORY_TYPE;
				return COMEBACK;
			case SET_CATEGORY_TYPE:
				delete();
				if (hasCallbackQuery()) {
					categoryType = list.get(Integer.parseInt(updateMessageText));
					categoryId = getCategoryId(categoryType);
					dateKeyboard = new DateKeyboard();
					deleteMessageId = sendStartDate();
					waitingType = WaitingType.START_DATE;
				}
				return COMEBACK;
			case START_DATE:
				delete();
				if (hasCallbackQuery()) {
					if (dateKeyboard.isNext(updateMessageText)) {
						sendStartDate();
						return COMEBACK;
					}
					start = dateKeyboard.getDateDate(updateMessageText);
					start.setHours(0);
					start.setMinutes(0);
					start.setSeconds(0);
					deleteMessageId = sendEndDate();
					waitingType = WaitingType.END_DATE;
				}
				return COMEBACK;
			case END_DATE:
				delete();
				if (hasCallbackQuery()) {
					if (dateKeyboard.isNext(updateMessageText)) {
						sendEndDate();
						return COMEBACK;
					}
					end = dateKeyboard.getDateDate(updateMessageText);
					end.setHours(23);
					end.setMinutes(59);
					end.setSeconds(59);
					sendReport();
					waitingType = WaitingType.END_DATE;
				}
				return COMEBACK;
		}
		
		return EXIT;
	}
	
	private void sendReport() throws TelegramApiException {
		int preview = sendMessage("Отчет подготавливается...");
		TaskReportService taskReportService = new TaskReportService();
		taskReportService.sendTaskReport(chatId, bot, start, end, categoryId, preview);
	}
	
	private void delete(){
		deleteMessage(updateMessageId);
		deleteMessage(deleteMessageId);
	}
	
	private int sendEndDate() throws TelegramApiException {
		//messageId 535 - Выберите конечную дату
		return toDeleteKeyboard(sendMessageWithKeyboard(getText(535), dateKeyboard.getCalendarKeyboard()));
	}
	
	private int sendStartDate() throws TelegramApiException {
		// messageId 534 - Выберите начальную дату, для подробного отчета
		return toDeleteKeyboard(sendMessageWithKeyboard(getText(534),dateKeyboard.getCalendarKeyboard()));
	}
	
	private int getCategoryType() throws TelegramApiException{
		list = new ArrayList<>();
		categoryDao.getAll().forEach(category -> list.add(category.getName()));
		buttonsLeaf = new ButtonsLeaf(list);
		return toDeleteKeyboard(sendMessageWithKeyboard("Выберите категорию для отчета", buttonsLeaf.getListButton()));
	}
	
	private int getCategoryId(String categoryType) {
		return categoryDao.getCategoryByName(categoryType).getId();
	}
}
