package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Category;
import baliviya.com.github.eduBot.entity.custom.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDao extends AbstractDao<Task> {

    public int insert(Task task) {
        sql = "INSERT INTO TASK (ID_STATUS, TASK_TEXT, DATE_BEGIN, PEOPLE_ID, PEOPLE_NAME, MESSAGE_ID, EMPLOYEE_ID, CATEGORY_ID) VALUES (?,?,?,?,?,?,?,?)";
        return (int) getDBUtils().updateForKeyId(sql, task.getStatusId(), task.getTaskText(), task.getDateBegin(), task.getPeopleId(), task.getPeopleName(), task.getMessageId(), task.getEmployeeId(), task.getCategoryId());
    }

    public List<Task> getAllTasks(int idStatus, long employeeChatId){
        sql = "SELECT * FROM TASK WHERE ID_STATUS = ? AND EMPLOYEE_ID = ?";
        return getJdbcTemplate().query(sql,setParam(idStatus, employeeChatId),this::mapper);
    }
    public Task get(int id){
        sql = "SELECT * FROM TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql,setParam(id),this::mapper);
    }

    public void updateStatus(int taskId, int idStatus){
        sql = "UPDATE TASK SET ID_STATUS = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, idStatus, taskId);
    }

    public List<Task> getAll(long employee_id){
        sql = "SELECT * FROM TASK WHERE EMPLOYEE_ID = ?";
        return getJdbcTemplate().query(sql, setParam(employee_id),this::mapper);
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
        task.setEmployeeId(rs.getInt(9));
        return task;
    }
}
