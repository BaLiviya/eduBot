package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Departments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentsDao extends AbstractDao<Departments> {

    public List<Departments>        getAll(){
        sql = "SELECT * FROM DEPARTMENTS WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public Departments        getDepartmentsById(int departmentId){
        sql = "SELECT * FROM DEPARTMENTS WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(departmentId, getLanguage().getId()), this::mapper);
    }

    @Override
    protected Departments mapper(ResultSet rs, int index) throws SQLException {
        Departments departments = new Departments();
        departments.setId(rs.getInt(1));
        departments.setName(rs.getString(2));
        departments.setLang_id(rs.getInt(3));
        return departments;
    }
}
