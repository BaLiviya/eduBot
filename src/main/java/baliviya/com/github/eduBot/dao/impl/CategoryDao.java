package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryDao extends AbstractDao<Category> {

    public Category            getText(Category category) {
        sql = "SELECT * FROM CATEGORY WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(category.getName(), getLanguage().getId()), this::mapper);
    }

    public Category            getTextById(Category category) {
        sql = "SELECT * FROM CATEGORY WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(category.getName(), category.getLangId()), this::mapper);
    }

    public List<Category>            getAll() {
        sql = "SELECT * FROM CATEGORY WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public void                      delete(int id){
        sql = "DELETE FROM CATEGORY WHERE ID = ?";
        getJdbcTemplate().update(sql, id);
    }

    public void                      insertRU(Category category){
        sql = "INSERT INTO CATEGORY(NAME, LANG_ID) VALUES(?,?)";
        getJdbcTemplate().update(sql, category.getName(), category.getLangId());
    }

    public void                      insertKZ(Category category){
        sql = "INSERT INTO CATEGORY(ID, NAME, LANG_ID) VALUES(?, ?, ?)";
        getJdbcTemplate().update(sql,category.getId(), category.getName(), category.getLangId());
    }

    public void                      update(Category category){
        sql = "UPDATE CATEGORY SET NAME = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, category.getName(), category.getId());
    }

    @Override
    protected Category mapper(ResultSet rs, int index) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt(1));
        category.setName(rs.getString(2));
        category.setLangId(rs.getInt(3));
        return category;
    }
}
