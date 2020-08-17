package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Document;
import baliviya.com.github.eduBot.entity.custom.TempMessage;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class id502_UploadFileToHelpLine extends Command {
	
	private int secondDeleteMessage;
	private Document document;
	private int buttonId;
	TempMessage tempMessageByChatId;
	private StringBuilder sb = new StringBuilder();
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		switch (waitingType){
			case START:
				sb.append(updateMessageId).append(Const.SPLIT);
				secondDeleteMessage = sendMessage(getText(Const.ATTACH_A_PDF_FILE_TEXT));
				sb.append(secondDeleteMessage).append(Const.SPLIT);
				isCommand();
				delete();
				return COMEBACK;
			case WAIT_TO_UPLOAD_FILE:
				if (hasDocument()) {
					sb.append(updateMessageId).append(Const.SPLIT);
					delete();
					log.info("success");
					document = new Document();
					document.setName(updateMessageDocument.getFileName());
					document.setDocument(updateMessageDocument.getFileId());
					document.setChat_id(chatId);
					document.setButton_id(buttonId);
					documentDao.insert(document);
					if (hasDocument()) {
						waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
						return COMEBACK;
					} else return EXIT;
				} else {
					delete();
					deleteMessages();
					//toDeleteKeyboard(sendMessageWithKeyboard(getText(528),510)); //Добавление файлов в меню "Гражданам"
					isCommand();
					waitingType = WaitingType.START;
				}
				return EXIT;
		}
		return EXIT;
	}
	
	private void deleteMessages(){
		if (tempMessageDao.isExist(chatId)){
			tempMessageByChatId = tempMessageDao.getTempMessageByChatId(chatId);
			if (tempMessageByChatId.getMessageId() != null) {
				String[] split = tempMessageByChatId.getMessageId().split(Const.SPLIT);
				for (int index = 0; index < split.length; index ++){
					deleteMessage(Integer.parseInt(split[index]));
				}
				tempMessageByChatId.setMessageId(null);
				tempMessageDao.update(tempMessageByChatId);
			}
		} else {
			tempMessageByChatId = new TempMessage();
			tempMessageByChatId.setChatId(chatId);
			tempMessageByChatId.setMessageId(null);
			tempMessageDao.insert(tempMessageByChatId);
		}
	}
	
	private void delete(){
		tempMessageByChatId.setMessageId(sb.toString());
		tempMessageDao.update(tempMessageByChatId);
	}
	
	private boolean isCommand() {
//		try {
//			secondDeleteMessage = sendMessage(getText(Const.ATTACH_A_PDF_FILE_TEXT));
//			sb.append(secondDeleteMessage).append(Const.SPLIT);
//		} catch (TelegramApiException e) {
//			e.printStackTrace();
//		}
		// Кнопки гражданам
		if (isButton(534)){
			deleteMessages();
			buttonId = 1;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(535)){
			deleteMessages();
			buttonId = 2;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(536)){
			deleteMessages();
			buttonId = 3;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(537)) {
			deleteMessages();
			buttonId = 4;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(538)){
			deleteMessages();
			buttonId = 5;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(539)) {
			deleteMessages();
			buttonId = 6;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(540)) {
			deleteMessages();
			buttonId = 7;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(541)) {
			deleteMessages();
			buttonId = 8;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(542)) {
			deleteMessages();
			buttonId = 9;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(543)) {
			deleteMessages();
			buttonId = 10;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		}
		// Кнопки нормативные документы
		else if (isButton(544)){
			deleteMessages();
			buttonId = 11;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(545)) {
			deleteMessages();
			buttonId = 12;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(546)) {
			deleteMessages();
			buttonId = 13;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		} else if (isButton(547)) {
			deleteMessages();
			buttonId = 14;
			waitingType = WaitingType.WAIT_TO_UPLOAD_FILE;
			return EXIT;
		}
		return EXIT;
	}
}
