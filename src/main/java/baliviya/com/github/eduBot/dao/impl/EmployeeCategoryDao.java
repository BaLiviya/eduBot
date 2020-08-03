package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeCategoryDao extends AbstractDao<EmployeeCategory> {

    public List<EmployeeCategory> getByCategoryId(int categoryId) {
        sql = "SELECT * FROM EMPLOYEE_CATEGORY WHERE CATEGORY_ID = ?";
        return getJdbcTemplate().query(sql, setParam(categoryId), this::mapper);
    }

    @Override
    protected EmployeeCategory mapper(ResultSet rs, int index) throws SQLException {
        EmployeeCategory employeeCategory = new EmployeeCategory();
        employeeCategory.setId(rs.getInt(1));
        employeeCategory.setCategoryId(rs.getLong(2));
        employeeCategory.setEmployeeChatId(rs.getLong(3));
        return employeeCategory;
    }
}