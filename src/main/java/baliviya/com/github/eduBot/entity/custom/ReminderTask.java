package baliviya.com.github.eduBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class ReminderTask {
	
	private int id;
	private String text;
	private Date  date_begin;
	private Date date_end;
	private String time_begin;
}
