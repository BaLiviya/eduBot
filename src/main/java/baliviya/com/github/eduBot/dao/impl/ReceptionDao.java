package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Reception;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReceptionDao extends AbstractDao<Reception> {

    public List<Reception> getAll() {
        sql  = "SELECT * FROM RECEPTION WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql,setParam(getLanguage().getId()), this::mapper);
    }

    public Reception getById(int receptionId) {
        sql = "SELECT * FROM RECEPTION WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(receptionId, getLanguage().getId()), this::mapper);
    }

    public void delete(int receptionId){
        sql = "DELETE FROM RECEPTION WHERE ID = ?";
        getJdbcTemplate().update(sql, receptionId);
    }

    public void insert(Reception reception){
        sql = "INSERT INTO RECEPTION(NAME) VALUES(?)";
        getJdbcTemplate().update(sql, reception.getName());
    }

    public void update(Reception reception) {
        sql = "UPDATE RECEPTION SET NAME = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, reception.getName(), reception.getId());
    }

    @Override
    protected Reception mapper(ResultSet rs, int index) throws SQLException {
        Reception reception = new Reception();
        reception.setId(rs.getInt(1));
        reception.setName(rs.getString(2));
        reception.setLang_id(rs.getInt(3));
        return reception;
    }
}
