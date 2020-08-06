package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.TaskArchive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class TaskArchiveDao extends AbstractDao<TaskArchive> {

    public void                  insert(String text, int taskId){
        sql = "INSERT INTO TASK_ARCHIVE (TEXT, TASK_ID) VALUES(?,?)";
        getJdbcTemplate().update(sql, text, taskId);
    }

    public void                  insert(String text, int taskId, String date){
        sql = "INSERT INTO TASK_ARCHIVE (TEXT, TASK_ID, DATE ) VALUES(?,?,?)";
        getJdbcTemplate().update(sql, text, taskId, date);
    }

    public List<TaskArchive>  getTasksArchive(int id){
        sql = "SELECT * FROM TASK_ARCHIEVE WHERE TASK_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public TaskArchive getTaskArchive(int id) {
        sql = "SELECT * FROM TASK_ARCHIVE WHERE TASK_ID = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) return null;
            return null;
        }
    }

    @Override
    protected TaskArchive mapper(ResultSet rs, int index) throws SQLException {
        TaskArchive taskArchive = new TaskArchive();
        taskArchive.setId(rs.getInt(1));
        taskArchive.setText(rs.getString(2));
        taskArchive.setTaskId(rs.getInt(3));
        taskArchive.setId(rs.getInt(4));
        return taskArchive;
    }
}
