package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.ReminderTask;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import baliviya.com.github.eduBot.util.components.DateKeyboard;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class id504_Reminder extends Command {
	
	private ReminderTask reminderTask;
	private DateKeyboard dateKeyboard;
	private Date dateStart;
	private List<ReminderTask> reminderTaskList;
	private int deleteMessageId;
	private int reminderTaskId;
	private boolean isUpdate = false;
	
	private List<User> users;
	private Date start;
	private List<ReminderTask> tasks;
	private StringBuilder sb;
	private Date now;
	private SimpleDateFormat simpleDateFormat;
	private Timer timer;
	
	@Override
	public boolean execute() throws TelegramApiException, IOException, SQLException {
		
		if (!isAdmin()) {
			sendMessage(Const.NO_ACCESS);
			return EXIT;
		}
		
		switch (waitingType) {
			case START:
				deleteMessage(updateMessageId);
				sendListReminder();
				waitingType = WaitingType.CHOICE_OPTION;
				return COMEBACK;
			case CHOICE_OPTION:
				deleteMessage(deleteMessageId);
				deleteMessage(updateMessageId);
				if (hasMessageText()) {
					timer = new Timer();
					if (isCommand("/new")) {
						dateKeyboard = new DateKeyboard();
						sendStartDate();
						waitingType = WaitingType.START_DATE;
					} else if (isCommand("/del")) {
						reminderTaskId = reminderTaskList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""))).getId();
						factory.getReminderTaskDao().delete(reminderTaskId);
						sendListReminder();
						waitingType = WaitingType.CHOICE_OPTION;
					} else if (isCommand("/st")) {
						reminderTaskId = reminderTaskList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""))).getId();
						dateKeyboard = new DateKeyboard();
						sendStartDate();
						isUpdate = true;
						waitingType = WaitingType.START_DATE;
					}
				}
				return COMEBACK;
			case START_DATE:
				deleteMessage(updateMessageId);
				if (hasCallbackQuery()) {
					if (dateKeyboard.isNext(updateMessageText)) {
						sendStartDate();
						return COMEBACK;
					}
					dateStart = dateKeyboard.getDateDate(updateMessageText);
					if (isUpdate) {
						reminderTask = factory.getReminderTaskDao().getReminderById(reminderTaskId);
					} else {
						reminderTask = new ReminderTask();
					}
					InlineKeyboardMarkup markup = sendInlineButtons(4,6,1);
					toDeleteKeyboard(sendMessageWithKeyboard("Выберите час отправки рассылки", markup));
//					sendMessage("напишите время рассылки (например 15:00)");
					waitingType = WaitingType.SET_HOUR;
				}
				return COMEBACK;
			case SET_HOUR:
				deleteMessage(updateMessageId);
				if (hasCallbackQuery()) {
					sb = new StringBuilder();
					sb.append(updateMessageText);
					sb.append(":");
					dateStart.setHours(Integer.parseInt(updateMessageText));
					InlineKeyboardMarkup markup = sendInlineButtons(3,4,0);
					toDeleteKeyboard(sendMessageWithKeyboard("Выберите минуту отправки", markup));
					waitingType = WaitingType.SET_MINUTE;
				}
				return COMEBACK;
			case SET_MINUTE:
				deleteMessage(updateMessageId);
				if (hasCallbackQuery()) {
					sb.append(updateMessageText);
					dateStart.setMinutes(Integer.parseInt(updateMessageText));
					dateStart.setSeconds(0);
					reminderTask.setDate_begin(dateStart);
					reminderTask.setTime_begin(sb.toString());
					sendMessage(getText(533)); //Введите текст сообщения
					waitingType = WaitingType.SET_TEXT;
				}
				return COMEBACK;
			case SET_TEXT:
				deleteMessage(updateMessageId);
				if (hasMessageText()) {
					reminderTask.setText(updateMessageText);
					if (isUpdate) {
						TimerTask task = new TimerTask() {
							@Override
							public void run() {
								task();
							}
						};
						timer.cancel();
						timer = new Timer();
						long delay = getDiffDate(new Date(), dateStart);
						try {
							timer.schedule(task, delay);
							factory.getReminderTaskDao().update(reminderTask);
						} catch (Exception e){
							sendMessage("Обновить не удалось");
						}
						isUpdate = false;
					}else {
						TimerTask timerTask = new TimerTask() {
							@Override
							public void run() {
								task();
							}
						};
						Timer timerNew = new Timer();
						long delay = getDiffDate(new Date(), dateStart);
						try {
							timerNew.schedule(timerTask, delay);
							factory.getReminderTaskDao().insert(reminderTask);
						} catch (IllegalArgumentException e) {
							sendMessage("нельзя запланировать");
						}
					}
					sendListReminder();
					waitingType = WaitingType.CHOICE_OPTION;
				}
				return COMEBACK;
		}
		return EXIT;
	}
	
	private long getDiffDate(Date start, Date stop) {
		long diff = stop.getTime() - start.getTime();
		return diff;
	}
	
	private int sendStartDate() throws TelegramApiException {
		return toDeleteKeyboard(sendMessageWithKeyboard(getText(532), dateKeyboard.getCalendarKeyboard())); // Выберите начальную дату, для подробного отчета
	}
	
	private boolean isCommand(String command) {
		return updateMessageText.startsWith(command);
	}
	
	private void sendListReminder() throws TelegramApiException {
		String formatMessage = getText(530); // text список обращений
		StringBuilder sb = new StringBuilder();
		reminderTaskList = factory.getReminderTaskDao().getAll();
		String format = getText(531);
		for (int index = 0; index < reminderTaskList.size(); index++) {
			ReminderTask rTask = reminderTaskList.get(index);
			sb.append(String.format(format, "/del" + index, "/st" + index, rTask.getText())).append(next);
		}
		deleteMessageId = sendMessage(String.format(formatMessage, sb.toString(), "/new"));
	}
	
	private InlineKeyboardMarkup sendInlineButtons(int countRow, int countColumn, int hourOrMinute){
		InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
		InlineKeyboardButton inlineKeyboardButton = null;
		List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
		int time = hourOrMinute;
		for (int i = 0; i < countRow; i++){
			List<InlineKeyboardButton> row1 = new ArrayList<>();
			for (int j = 0; j < countColumn; j++){
				inlineKeyboardButton = new InlineKeyboardButton();
				inlineKeyboardButton.setText(String.format("%02d", time));
				inlineKeyboardButton.setCallbackData(String.format("%02d", time));
				row1.add(inlineKeyboardButton);
				if (hourOrMinute == 0) {
					time+=5;
				} else {
					time++;
				}
			}
			rowList.add(row1);
		}
		
		keyboardMarkup.setKeyboard(rowList);
		return keyboardMarkup;
	}
	
	private void task(){
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateNow = simpleDateFormat.format(new Date());
		String dateStartString = simpleDateFormat.format(new Date());
		start = new Date();
		users = userDao.getAll();
		try {
			now = simpleDateFormat.parse(dateNow);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			start = simpleDateFormat.parse(dateStartString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date dateEnd = new Date();
		dateEnd.setDate(start.getDate() + 1);
		String dateEndString = simpleDateFormat.format(dateEnd);
		Date finalDate = null;
		try {
			finalDate = simpleDateFormat.parse(dateEndString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		tasks = factory.getReminderTaskDao().getByTime(start, finalDate);
		
		checkMessage();
	}
	
	private void checkMessage() {
		if (tasks != null && tasks.size() != 0) {
			for (User user : users) {
				sendMessage(user);
			}
		}
	}
	
	private void sendMessage(User user) {
		tasks.forEach(e -> {
			//					Date beginDate = simpleDateFormat.parse(e.getDate_begin());
			if ((DateUtil.getDayDate(e.getDate_begin()).equals(DateUtil.getDayDate(now))) && e.getTime_begin().equals(DateUtil.getString(now, "HH:mm"))) {
				try {
					sendMessage(e.getText(), user.getChatId());
//					bot.execute(new SendMessage().setChatId(user.getChatId()).setText(e.getText()));
				} catch (TelegramApiException telegramApiException) {
					telegramApiException.printStackTrace();
				}
			}
		});
	}
}
