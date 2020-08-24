package baliviya.com.github.eduBot.util.reminders;

import baliviya.com.github.eduBot.config.Bot;
import baliviya.com.github.eduBot.util.DateUtil;
import baliviya.com.github.eduBot.util.reminders.timerTask.MorningTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Timer;

@Slf4j
public class Reminder {
	
	private Bot bot;
	private Timer timer = new Timer(true);
	
	public Reminder(Bot bot) {
		this.bot = bot;
		setMorningTask();
	}
	
	public void setMorningTask() {
//		Date date = DateUtil.getHour(hour);
		log.info(String.format("next check db task set to %s", new Date()));
		MorningTask checkMorningTask = new MorningTask(bot, this);
		timer.schedule(checkMorningTask, new Date());
		
		
	}
}
