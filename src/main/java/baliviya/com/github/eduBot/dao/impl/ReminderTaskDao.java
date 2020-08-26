package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.ReminderTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ReminderTaskDao extends AbstractDao<ReminderTask> {
	
	public List<ReminderTask> getAll() {
		sql = "SELECT * FROM REMINDER_TASK";
		return getJdbcTemplate().query(sql,this::mapper);
	}
	
	public void delete(int reminderTaskId) {
		sql = "DELETE FROM REMINDER_TASK WHERE ID = ?";
		getJdbcTemplate().update(sql,setParam(reminderTaskId));
	}
	
	public ReminderTask getReminderById(int reminderTaskId) {
		sql = "SELECT * FROM REMINDER_TASK WHERE ID = ?";
		return getJdbcTemplate().queryForObject(sql, setParam(reminderTaskId), this::mapper);
	}
	
	public void insert(ReminderTask reminderTask) {
		sql = "INSERT INTO REMINDER_TASK(TEXT, DATE_BEGIN, TIME_BEGIN) VALUES(?, ?, ?)";
		getJdbcTemplate().update(sql, reminderTask.getText(), reminderTask.getDate_begin(), reminderTask.getTime_begin());
	}
	
	public void update(ReminderTask reminderTask) {
		sql = "UPDATE REMINDER_TASK SET TEXT = ?, DATE_BEGIN = ?, TIME_BEGIN = ? WHERE ID = ?";
		getJdbcTemplate().update(sql, reminderTask.getText(), reminderTask.getDate_begin(), reminderTask.getTime_begin(), reminderTask.getId());
	}
	
	public List<ReminderTask>   getByTime(Date dateBegin, Date dateEnd) {
		sql = "SELECT * FROM REMINDER_TASK WHERE DATE_BEGIN BETWEEN (?) AND (?)";
		return getJdbcTemplate().query(sql, setParam(dateBegin, dateEnd), this::mapper);
	}
	
	@Override
	protected ReminderTask mapper(ResultSet rs, int index) throws SQLException {
		ReminderTask reminderTask = new ReminderTask();
		reminderTask.setId(rs.getInt(1));
		reminderTask.setText(rs.getString(2));
		reminderTask.setDate_begin(rs.getDate(3));
		reminderTask.setDate_end(rs.getDate(4));
		reminderTask.setTime_begin(rs.getString(5));
		return reminderTask;
	}
}
