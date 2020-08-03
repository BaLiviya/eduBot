package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Task;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskDao extends AbstractDao<Task> {

    public int insert(Task task) {
        sql = "INSERT INTO TASK (ID_STATUS, TASK_TEXT, DATE_BEGIN, PEOPLE_ID, PEOPLE_NAME, MESSAGE_ID, EMPLOYEE_ID) VALUES (?,?,?,?,?,?,?)";
        return (int) getDBUtils().updateForKeyId(sql, task.getStatusId(), task.getTaskText(), task.getDateBegin(), task.getPeopleId(), task.getPeopleName(), task.getMessageId(), task.getEmployeeId());
    }

    @Override
    protected Task mapper(ResultSet rs, int index) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt(1));
        task.setStatusId(rs.getInt(2));
        task.setTaskText(rs.getString(3));
        task.setDateBegin(rs.getDate(4));
        task.setPeopleId(rs.getLong(5));
        task.setPeopleName(rs.getString(6));
        task.setCategoryId(rs.getInt(7));
        task.setMessageId(rs.getInt(8));
        task.setEmployeeId(rs.getLong(9));
        return task;
    }
}
