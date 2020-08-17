package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Position;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PositionDao extends AbstractDao<Position> {

    public List<Position> getAll(){
        sql = "SELECT * FROM POSITION WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public List<Position> getMenu(int menuId){
        sql = "SELECT * FROM POSITION WHERE   MENU_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(menuId, getLanguage().getId()), this::mapper);
    }

    public Position getPosition(int id){
        sql = "SELECT * FROM POSITION WHERE ID = ? AND LANG_ID = ? ";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()),this::mapper);
    }

    @Override
    protected Position mapper(ResultSet rs, int index) throws SQLException {
        Position position = new Position();
        position.setId(rs.getInt(1));
        position.setPosition(rs.getString(2));
        position.setLangId(rs.getInt(3));
        position.setMenuId(rs.getInt(4));
        return position;
    }
}
