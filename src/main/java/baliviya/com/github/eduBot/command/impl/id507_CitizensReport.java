package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.service.CitizensReportService;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.components.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class id507_CitizensReport extends Command {
	
	private int deleteMessageId;
	private DateKeyboard dateKeyboard;
	private Date start, end;
	private String suggestionType;
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		
		if (!isAdmin()) {
			sendMessage(Const.NO_ACCESS);
			return EXIT;
		}
		
		switch (waitingType){
			case START:
				deleteMessage(updateMessageId);
				dateKeyboard = new DateKeyboard();
				deleteMessageId = sendStartDate();
				waitingType = WaitingType.START_DATE;
				return COMEBACK;
			case START_DATE:
				delete();
				if (hasCallbackQuery()) {
					if (dateKeyboard.isNext(updateMessageText)) {
						deleteMessageId = sendStartDate();
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
					if (dateKeyboard.isNext(updateMessageText)){
						deleteMessageId = sendEndDate();
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
		int preview         = sendMessage("Отчет подготавливается");
		CitizensReportService citizensReportService = new CitizensReportService();
		citizensReportService.sendCitizensReport(chatId,bot,start, end, preview);
	}
	
	private int sendStartDate() throws TelegramApiException {
		return toDeleteKeyboard(sendMessageWithKeyboard(getText(534), dateKeyboard.getCalendarKeyboard()));
	}
	
	private int sendEndDate() throws TelegramApiException {
		return toDeleteKeyboard(sendMessageWithKeyboard(getText(535), dateKeyboard.getCalendarKeyboard()));
	}
	
	private void delete(){
		deleteMessage(updateMessageId);
		deleteMessage(deleteMessageId);
	}
}
