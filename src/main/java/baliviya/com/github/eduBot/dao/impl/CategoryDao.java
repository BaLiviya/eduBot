package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Category;
import baliviya.com.github.eduBot.service.LanguageService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryDao extends AbstractDao<Category> {

    public List<Category> getAll() {
        sql = "SELECT * FROM CATEGORY WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }
    
    public Category getCategoryById(int id){
        sql = "SELECT * FROM CATEGORY WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql,setParam(id, getLanguage().getId()), this::mapper);
    }
    
    public Category getCategoryByName(String name){
        sql = "SELECT * FROM CATEGORY WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(name, getLanguage().getId()), this::mapper);
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
