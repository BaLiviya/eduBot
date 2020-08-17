package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.CitizensButton;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CitizensButtonsDao extends AbstractDao<CitizensButton> {
	
	
	public CitizensButton getCitizensButtonIdByName(String name){
		sql = "SELECT * FROM CITIZENS_BUTTON WHERE NAME = ?";
		return getJdbcTemplate().queryForObject(sql, setParam(name), this::mapper);
	}
	@Override
	protected CitizensButton mapper(ResultSet rs, int index) throws SQLException {
		CitizensButton button = new CitizensButton();
		button.setId(rs.getInt(1));
		button.setName(rs.getString(2));
		return button;
	}
}
