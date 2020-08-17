package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Document;
import baliviya.com.github.eduBot.entity.custom.TempMessage;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class id503_GetFileForHelpLine extends Command {
	
	private List<Document> docs;
	private Document document;
	private ButtonsLeaf buttonsLeaf;
	private int citizensButtonId;
	private int secondDeleteMessageId;
	private int deleteMessageId ;
	private TempMessage tempMessageByChatId;
	private StringBuilder sb = new StringBuilder();
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		switch (waitingType) {
			case START:
				sb.append(updateMessageId).append(Const.SPLIT);
				isCommand();
				docs = documentDao.getAllDocumentsByButtonId(citizensButtonId);
				if (docs.isEmpty()) {
					secondDeleteMessageId = sendMessage(getText(602));
					sb.append(secondDeleteMessageId).append(Const.SPLIT);
					delete();
					return EXIT;
				}
				List<String> list = new ArrayList<>();
				docs.forEach(doc -> list.add(doc.getName()));
				buttonsLeaf = new ButtonsLeaf(list);
				deleteMessageId = sendMessageWithKeyboard(Const.FILES_MESSAGE, buttonsLeaf.getListButton());
				sb.append(deleteMessageId).append(Const.SPLIT);
//				toDeleteMessage(sendMessageWithKeyboard(Const.FILES_MESSAGE, buttonsLeaf.getListButton()));
				delete();
				waitingType = WaitingType.GET_FILE;
				return COMEBACK;
			case GET_FILE:
				if (hasCallbackQuery()) {
					deleteMessages();
					document = docs.get(Integer.parseInt(updateMessageText));
					if (document.getDocument() != null) {
						secondDeleteMessageId = bot.execute(new SendDocument().setChatId(chatId).setDocument(document.getDocument())).getMessageId();
						sb.append(secondDeleteMessageId).append(Const.SPLIT);
						delete();
						waitingType = WaitingType.START;
					}
				} else {
					deleteMessages();
					isCommand();
					docs = documentDao.getAllDocumentsByButtonId(citizensButtonId);
					List<String> stringList = new ArrayList<>();
					docs.forEach(e -> stringList.add(e.getName()));
					if (stringList.isEmpty()){
						secondDeleteMessageId = sendMessage(getText(602));
						sb.append(secondDeleteMessageId).append(Const.SPLIT);
						delete();
						return EXIT;
					}
					deleteMessageId = sendMessageWithKeyboard(Const.FILES_MESSAGE, buttonsLeaf.getListButton());
					sb.append(deleteMessageId).append(Const.SPLIT);
					delete();
//					toDeleteMessage(sendMessageWithKeyboard(Const.FILES_MESSAGE, buttonsLeaf.getListButton()));
				}
				
				return COMEBACK;
		}
		return EXIT;
		
	}
	
	private void delete() {
		tempMessageByChatId.setMessageId(sb.toString());
		tempMessageDao.update(tempMessageByChatId);
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
	
	private boolean isCommand() {
//		delete();
//		deleteMessages();
		if (isButton(508)) {
			deleteMessages();
			citizensButtonId = 1;
			return EXIT;
		} else if (isButton(509)) {
			deleteMessages();
			citizensButtonId = 2;
			return EXIT;
		} else if (isButton(510)) {
			deleteMessages();
			citizensButtonId = 3;
			return EXIT;
		} else if (isButton(511)) {
			deleteMessages();
			citizensButtonId = 4;
			return EXIT;
		} else if (isButton(513)) {
			deleteMessages();
			citizensButtonId = 5;
			return EXIT;
		} else if (isButton(514)) {
			deleteMessages();
			citizensButtonId = 6;
			return EXIT;
		} else if (isButton(515)) {
			deleteMessages();
			citizensButtonId = 7;
			return EXIT;
		} else if (isButton(516)) {
			deleteMessages();
			citizensButtonId = 8;
			return EXIT;
		} else if (isButton(517)) {
			deleteMessages();
			citizensButtonId = 9;
			return EXIT;
		} else if (isButton(518)) {
			deleteMessages();
			citizensButtonId = 10;
			return EXIT;
		}
		// Кнопки нормативные документы
		else if (isButton(519)){
			deleteMessages();
			citizensButtonId = 11;
			return EXIT;
		} else if (isButton(520)) {
			deleteMessages();
			citizensButtonId = 12;
			return EXIT;
		} else if (isButton(521)) {
			deleteMessages();
			citizensButtonId = 13;
			return EXIT;
		} else if (isButton(522)) {
			deleteMessages();
			citizensButtonId = 14;
			return EXIT;
		}
		return EXIT;
	}
	
}
