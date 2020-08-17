package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EventDao extends AbstractDao<Event> {

    public List<Event> getAll() {
        sql = "SELECT * FROM EVENT";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<Event> getAllActive() {
        sql = "SELECT * FROM EVENT WHERE IS_HIDE = FALSE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void delete(int eventId) {
        sql = "DELETE FROM EVENT WHERE ID = ?";
        getJdbcTemplate().update(sql, eventId);
    }

    public void updateStatus(Event event) {
        sql = "UPDATE EVENT SET IS_HIDE = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, event.isHide(), event.getId());
    }
    
    public void insert(Event event) {
        sql = "INSERT INTO EVENT(NAME,PHOTO,TEXT,IS_HIDE) VALUES(?,?,?,?)";
        getJdbcTemplate().update(sql, event.getName(), event.getPhoto(), event.getText(),event.isHide());
    }

    @Override
    protected Event mapper(ResultSet rs, int index) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt(1));
        event.setName(rs.getString(2));
        event.setPhoto(rs.getString(3));
        event.setText(rs.getString(4));
        event.setHide(rs.getBoolean(5));
        return event;
    }
}
