package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.enums.Language;
import baliviya.com.github.eduBot.entity.standart.Button;
import baliviya.com.github.eduBot.exceptions.CommandNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ButtonDao extends AbstractDao<Button> {

    public Button       getButton(String text) throws CommandNotFoundException {
        sql = "SELECT * FROM BUTTON WHERE NAME = ? AND LANG_ID = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, setParam(text, getLanguage().getId()), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) throw new CommandNotFoundException(e);
            throw e;
        }
    }

    public Button       getButton(int id) {
        sql = "SELECT * FROM BUTTON WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public Button       getButton(int id, Language language) {
        sql = "SELECT * FROM BUTTON WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public String       getButtonText(int id) {
        sql = "SELECT NAME FROM BUTTON WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), String.class);
    }

    public boolean      isExist(String text, Language language) {
        sql = "SELECT count(*) FROM BUTTON WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(text, language.getId()), Integer.class) > 0;
    }

    public void         update(Button button) {
        sql = "UPDATE BUTTON SET NAME = ?, URL = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, button.getName(), button.getUrl(), button.getId(), button.getLanguage().getId());
    }

    @Override
    protected Button    mapper(ResultSet rs, int index) throws SQLException {
        Button button = new Button();
        button.setId(rs.getInt(1));
        button.setName(rs.getString(2));
        button.setCommandId(rs.getInt(3));
        button.setUrl(rs.getString(4));
        button.setRequestContact(rs.getBoolean(5));
        button.setMessageId(rs.getInt(6));
        button.setLanguage(Language.getById(rs.getInt(7)));
        return button;
    }
}
