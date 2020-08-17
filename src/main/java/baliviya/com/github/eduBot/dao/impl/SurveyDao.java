package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Survey;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SurveyDao extends AbstractDao<Survey> {

    public void insert(Survey survey){
        sql = "INSERT INTO SURVEY_ANSWER(CHAT_ID , ANSWERS , DATE_ANSWER) VALUES (?, ?, ?)";
        getJdbcTemplate().update(sql, survey.getChatId(), survey.getAnswers(), survey.getDateAnswer());
    }

    public int getCount(){
        sql ="SELECT COUNT (ID) FROM SURVEY_ANSWER";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public List<Survey> getAll(){
        sql = "SELECT * FROM SURVEY_ANSWER";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected Survey mapper(ResultSet rs, int index) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getInt(1));
        survey.setChatId(rs.getLong(2));
        survey.setAnswers(rs.getString(3));
        survey.setDateAnswer(rs.getDate(4));
        return survey;
    }
}
