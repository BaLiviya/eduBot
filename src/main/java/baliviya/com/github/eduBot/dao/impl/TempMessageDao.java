package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.TempMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TempMessageDao extends AbstractDao<TempMessage> {
	
	public void insert(TempMessage tempMessage){
		sql = "INSERT INTO TEMP_MESSAGES(CHAT_ID, MESSAGE_ID) VALUES (?, ?)";
		getJdbcTemplate().update(sql,tempMessage.getChatId(),tempMessage.getMessageId());
	}
	
	public void update(TempMessage tMessage){
		sql = "UPDATE TEMP_MESSAGES SET MESSAGE_ID = ? WHERE CHAT_ID = ?";
		getJdbcTemplate().update(sql, tMessage.getMessageId(), tMessage.getChatId());
	}
	
	public TempMessage getTempMessageByChatId(long chatId){
		sql = "SELECT * FROM TEMP_MESSAGES WHERE CHAT_ID = ?";
		return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
	}
	
	public boolean isExist(long chatId){
		sql = "SELECT count(*) FROM TEMP_MESSAGES WHERE CHAT_ID = ?";
		return getJdbcTemplate().queryForObject(sql,setParam(chatId),Integer.class) > 0;
	}
	@Override
	protected TempMessage mapper(ResultSet rs, int index) throws SQLException {
		TempMessage tMessage = new TempMessage();
		tMessage.setChatId(rs.getLong(1));
		tMessage.setMessageId(rs.getString(2));
		return tMessage;
	}
}
