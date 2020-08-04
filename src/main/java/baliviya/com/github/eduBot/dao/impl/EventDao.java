package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EventDao extends AbstractDao<Event> {

    public List<Event> getAllActive(){
        sql = "SELECT * FROM EVENT WHERE IS_HIDE = FALSE";
        return getJdbcTemplate().query(sql,this::mapper);
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
