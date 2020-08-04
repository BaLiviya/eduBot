package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.CitizensRegistration;
import org.checkerframework.checker.units.qual.C;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CitizensRegistrationDao extends AbstractDao<CitizensRegistration> {

    public void insert(CitizensRegistration citizensRegistration){
        sql = "INSERT INTO CITIZENS_REGISTRATION(CHAT_ID, RECEPTION_ID, FULL_NAME, QUESTION, STATUS, DATE, CITIZENS_DATE, CITIZENS_TIME) VALUES(?,?,?,?,?,?,?,?)";
        getJdbcTemplate().update(sql,citizensRegistration.getChatId(),citizensRegistration.getReceptionId(),citizensRegistration.getFullName(),citizensRegistration.getQuestion(),citizensRegistration.getStatus(),citizensRegistration.getDate(),citizensRegistration.getCitizensDate(),citizensRegistration.getCitizensTime());
    }

    public List<CitizensRegistration> getAll(int reception_id){
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE RECEPTION_ID = ? AND STATUS = 'Записан'";
        return getJdbcTemplate().query(sql,setParam(reception_id),this::mapper);
    }

    public List<CitizensRegistration> getRegistrationByTime(Date dateBegin, Date deadline, int reception_id) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE DATE BETWEEN to_date(?, 'YYYY-MM-DD HH:mm:SS') AND to_date(?, 'YYYY-MM-DD HH:mm:SS') AND RECEPTION_ID = ? ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(dateBegin,deadline,reception_id), this::mapper);
    }

    public CitizensRegistration getByReceptionId(int reception_id){
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE RECEPTION_ID = ? AND STATUS = 'Записан'";
        try {
            return getJdbcTemplate().queryForObject(sql, setParam(reception_id), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) return null;
            throw e;
        }
    }

    public CitizensRegistration getById(int id) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE ID = ? AND STATUS = 'Записан'";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public void update(CitizensRegistration citizensRegistration) {
        sql = "UPDATE CITIZENS_REGISTRATION SET STATUS = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, citizensRegistration.getStatus(), citizensRegistration.getId());
    }
    @Override
    protected CitizensRegistration mapper(ResultSet rs, int index) throws SQLException {
        CitizensRegistration citizensRegistration = new CitizensRegistration();
        citizensRegistration.setId(rs.getInt(1));
        citizensRegistration.setChatId(rs.getLong(2));
        citizensRegistration.setReceptionId(rs.getInt(3));
        citizensRegistration.setFullName(rs.getString(4));
        citizensRegistration.setQuestion(rs.getString(5));
        citizensRegistration.setStatus(rs.getString(6));
        citizensRegistration.setDate(rs.getDate(7));
        citizensRegistration.setCitizensDate(rs.getDate(8));
        citizensRegistration.setCitizensTime(rs.getString(9));
        return citizensRegistration;
    }
}
