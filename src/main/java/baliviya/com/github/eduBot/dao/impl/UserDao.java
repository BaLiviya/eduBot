package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.standart.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao extends AbstractDao<User> {

    public void         insert(User user) {
        sql = "INSERT INTO USERS (CHAT_ID, USER_NAME, PHONE, FULL_NAME) VALUES (?,?,?,?)";
        getJdbcTemplate().update(sql, user.getChatId(), user.getUserName(), user.getPhone(), user.getFullName());
    }

    public void         update(User user) {
        sql = "UPDATE USERS SET PHONE = ?, FULL_NAME = ?, USER_NAME = ? WHERE CHAT_ID = ?";
        getJdbcTemplate().update(sql, user.getPhone(), user.getFullName(), user.getUserName(), user.getChatId());
    }

    public User         getUserByChatId(long chatId) {
        sql = "SELECT * FROM USERS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    public boolean      isRegistered(long chatId) {
        sql = "SELECT count(*) FROM USERS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) > 0;
    }

    public int          count() {
        sql = "SELECT count(ID) FROM USERS";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public List<User>   getAll() {
        sql = "SELECT * FROM USERS";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected User      mapper(ResultSet rs, int index) throws SQLException {
        User user = new User();
        user.setId(rs.getInt(1));
        user.setChatId(rs.getLong(2));
        user.setPhone(rs.getString(3));
        user.setFullName(rs.getString(4));
        user.setUserName(rs.getString(5));
        return user;
    }
}
