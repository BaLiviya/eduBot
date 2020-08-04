package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.RegistrationEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationEventDao extends AbstractDao<RegistrationEvent> {

    public boolean isJoinToEvent(long chat_id, long event_id){
        sql = "SELECT count(*) FROM REGISTRATION_EVENT WHERE CHAT_ID = ? AND EVENT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chat_id, event_id), Integer.class) > 0;
    }
    public void insert(RegistrationEvent registrationEvent) {
        sql = "INSERT INTO REGISTRATION_EVENT(CHAT_ID,EVENT_ID,REGISTRATION_DATE,IS_COME) VALUES(?,?,?,?)";
        getJdbcTemplate().update(sql,registrationEvent.getChat_id(),registrationEvent.getEvent_id(),registrationEvent.getRegistration_date(),registrationEvent.isCome());
    }

    @Override
    protected RegistrationEvent mapper(ResultSet rs, int index) throws SQLException {
        RegistrationEvent registrationEvent =  new RegistrationEvent();
        registrationEvent.setId(rs.getInt(1));
        registrationEvent.setChat_id(rs.getLong(2));
        registrationEvent.setEvent_id(rs.getLong(3));
        registrationEvent.setRegistration_date(rs.getDate(4));
        registrationEvent.setCome(rs.getBoolean(5));
        return registrationEvent;
    }
}
