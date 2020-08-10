package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Quest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestDao extends AbstractDao<Quest> {

    public List<Quest> getAll(){
        sql = "SELECT * FROM SURVEY_QUEST";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<Quest> getAllSuggestionType(){
        sql = "SELECT * FROM SUGGESTION_TYPE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void insert (Quest quest){
        sql = "INSERT INTO SURVEY_QUEST(NAME) VALUES(?)";
        getJdbcTemplate().update(sql, quest.getName());
    }

    public void delete(int questId){
        sql = "DELETE FROM SURVEY_QUEST WHERE ID = ?";
        getJdbcTemplate().update(sql, questId);
    }

    public Quest getById(int id){
        sql ="SELECT * FROM SURVEY_QUEST WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public void update(Quest quest) {
        sql = "UPDATE SURVEY_QUEST  SET NAME = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, quest.getName(), quest.getId());
    }

    public void updateSuggestionType(Quest suggestionType){
        sql = "UPDATE SUGGESTION_TYPE SET NAME = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, suggestionType.getName(), suggestionType.getId());
    }

    public void deleteSuggestionType(int suggestionType){
        sql = "DELETE FROM SUGGESTION_TYPE WHERE ID = ?";
        getJdbcTemplate().update(sql, suggestionType);
    }

    public void insertSuggestionType(Quest suggestionType){
        sql = "INSERT INTO SUGGESTION_TYPE(NAME) VALUES(?)";
        getJdbcTemplate().update(sql, suggestionType.getName());
    }

    @Override
    protected Quest mapper(ResultSet rs, int index) throws SQLException {
        Quest appealType = new Quest();
        appealType.setId(rs.getInt(1));
        appealType.setName(rs.getString(2));
        return appealType;
    }
}
