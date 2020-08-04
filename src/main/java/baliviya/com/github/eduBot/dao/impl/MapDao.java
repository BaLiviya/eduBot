package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Map;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapDao extends AbstractDao<Map> {

    public Map getAll() {
        sql = "SELECT * FROM MAP_COORDINATE WHERE ID = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, setParam(1), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) return null;
            throw e;
        }
    }


    @Override
    protected Map mapper(ResultSet rs, int index) throws SQLException {
        Map map = new Map();
        map.setId(rs.getInt(1));
        map.setLatitude(rs.getFloat(2));
        map.setLongitude(rs.getFloat(3));
        //map.setChatId(rs.getLong(4));
        return map;
    }
}
