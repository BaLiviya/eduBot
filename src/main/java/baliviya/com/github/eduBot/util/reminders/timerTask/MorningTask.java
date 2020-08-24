package baliviya.com.github.eduBot.util.reminders.timerTask;

import baliviya.com.github.eduBot.config.Bot;
import baliviya.com.github.eduBot.entity.custom.ReminderTask;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.DateUtil;
import baliviya.com.github.eduBot.util.reminders.Reminder;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

@Slf4j
public class MorningTask extends AbstractTask {
	
	private List<User> users;
	private Date start;
	private List<ReminderTask> tasks;
	private StringBuilder sb;
	private Date now;
	private SimpleDateFormat simpleDateFormat;
	
	public MorningTask(Bot bot, Reminder reminder) {
		super(bot, reminder);
	}
	
	
	@Override
	public void run() {
		task();
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
					bot.execute(new SendMessage().setChatId(user.getChatId()).setText(e.getText()));
				} catch (TelegramApiException telegramApiException) {
					telegramApiException.printStackTrace();
				}
			}
		});
	}
}
