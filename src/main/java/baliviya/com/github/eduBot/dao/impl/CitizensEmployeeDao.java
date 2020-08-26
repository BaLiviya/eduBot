package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.CitizensEmployee;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CitizensEmployeeDao extends AbstractDao<CitizensEmployee>{
	@Override
	protected CitizensEmployee mapper(ResultSet rs, int index) throws SQLException {
		CitizensEmployee citizensEmployee = new CitizensEmployee();
		citizensEmployee.setId(rs.getInt(1));
		citizensEmployee.setChatId(rs.getLong(2));
		citizensEmployee.setReceptionId(rs.getInt(3));
		return citizensEmployee;
	}
	
	public List<CitizensEmployee> getList(int receptionId) {
		sql = "SELECT * FROM CITIZENS_EMPLOYEE WHERE RECEPTION_ID = ?";
		return getJdbcTemplate().query(sql,setParam(receptionId),this::mapper);
	}
	
	public void delete(long chatId, int receptionId) {
		sql = "DELETE FROM CITIZENS_EMPLOYEE WHERE CHAT_ID = ? AND RECEPTION_ID = ?";
		getJdbcTemplate().update(sql, chatId, receptionId);
	}
	
	public boolean isCitizenEmployee(long newEmpChatId, int receptionId) {
		sql = "SELECT count(*) FROM CITIZENS_EMPLOYEE WHERE CHAT_ID = ? AND RECEPTION_ID = ?";
		int count = getJdbcTemplate().queryForObject(sql, setParam(newEmpChatId, receptionId), Integer.class);
		if (count > 0) return true;
		return false;
	}
	
	public void addEmployee(long newEmpChatId, int receptionId) {
		sql = "INSERT INTO CITIZENS_EMPLOYEE VALUES (DEFAULT , ?, ?)";
		getJdbcTemplate().update(sql, newEmpChatId, receptionId);
	}
	
	public CitizensEmployee getByChatId(long chatId) {
		sql = "SELECT * FROM CITIZENS_EMPLOYEE WHERE CHAT_ID = ?";
		try {
			return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
		} catch (Exception e){
			return null;
		}
	}
}
