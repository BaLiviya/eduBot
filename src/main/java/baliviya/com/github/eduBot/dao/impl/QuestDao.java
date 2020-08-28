package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Quest;
import baliviya.com.github.eduBot.entity.enums.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class QuestDao extends AbstractDao<Quest> {



    public List<Quest> getAll(){
        sql = "SELECT * FROM SURVEY_QUEST WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public List<Quest> getAllByLangId(Language language){
        sql = "SELECT * FROM SURVEY_QUEST WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(language.getId()), this::mapper);
    }

    public Quest getById(int id){
        sql ="SELECT * FROM SURVEY_QUEST WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id,getLanguage().getId()), this::mapper);
    }

    public Quest getAllById(int id, Language language){
        sql = "SELECT * FROM SURVEY_QUEST WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public Quest getTextById(Quest quest){
        sql = "SELECT * FROM SURVEY_QUEST WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(quest.getName(), quest.getLangId()), this::mapper);
    }

    public int insertRu (Quest quest){
        sql = "INSERT INTO SURVEY_QUEST(NAME, LANG_ID) VALUES(?,?)";
        return (int) getDBUtils().updateForKeyId(sql, quest.getName(), quest.getLangId());
    }

    public void insertKz (Quest quest){
        sql  = "INSERT INTO SURVEY_QUEST(ID, NAME, LANG_ID) VALUES (?,?,?)";
        getJdbcTemplate().update(sql, quest.getId(), quest.getName(), quest.getLangId());
    }

    public void delete(int questId){
        sql = "DELETE FROM SURVEY_QUEST WHERE ID = ?";
        getJdbcTemplate().update(sql, questId);
    }

    public void update(Quest quest) {
        sql = "UPDATE SURVEY_QUEST  SET NAME = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, quest.getName(), quest.getId(), quest.getLangId());
    }



    @Override
    protected Quest mapper(ResultSet rs, int index) throws SQLException {
        Quest quest = new Quest();
        quest.setId(rs.getInt(1));
        quest.setName(rs.getString(2));
        quest.setLangId(rs.getInt(3));
        return quest;
    }
}
