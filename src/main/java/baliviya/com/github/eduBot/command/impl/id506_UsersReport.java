package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.service.UsersReportService;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id506_UsersReport extends Command {
	
	private List<User> users;
	private int deleteMessageId;
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		
		if (!isAdmin()){
			sendMessage(Const.NO_ACCESS);
			return EXIT;
		}
		
		deleteMessage(updateMessageId);
		users = userDao.getAll();
		deleteMessageId = sendMessage(String.format("Всего пользователей: %s", users.size()));
		delete();
		sendReport();
		return EXIT;
	}
	private void delete(){
		deleteMessage(updateMessageId);
		deleteMessage(deleteMessageId);
	}
	
	private void sendReport() throws TelegramApiException {
		int preview = sendMessage("Отчет подготавливается...");
		UsersReportService usersReportService = new UsersReportService();
		usersReportService.sendUserReport(chatId, bot, preview);
	}
}
