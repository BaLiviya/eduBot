package baliviya.com.github.eduBot.util.reminders.timerTask;

import baliviya.com.github.eduBot.config.Bot;
import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.ReminderTaskDao;
import baliviya.com.github.eduBot.dao.impl.TaskDao;
import baliviya.com.github.eduBot.dao.impl.UserDao;
import baliviya.com.github.eduBot.util.reminders.Reminder;

import java.util.TimerTask;

public abstract class AbstractTask extends TimerTask {
	
	protected Bot               bot;
	protected Reminder reminder;
	protected DaoFactory        factory         = DaoFactory.getInstance();
	//protected ReminderTaskDao   reminderTaskDao = factory.getReminderTaskDao();
	protected UserDao           userDao         =factory.getUserDao();
	protected TaskDao           taskDao         = factory.getTaskDao();
	
	public AbstractTask(Bot bot, Reminder reminder) {
		this.bot = bot;
		this.reminder = reminder;
	}
}
