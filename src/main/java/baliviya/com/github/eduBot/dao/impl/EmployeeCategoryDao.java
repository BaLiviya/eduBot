package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.EmployeeCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeCategoryDao extends AbstractDao<EmployeeCategory> {

    public List<EmployeeCategory>       getNonRepeatingId(){
        sql         = "SELECT DISTINCT EMPLOYEE_CHAT_ID FROM EMPLOYEE_CATEGORY ";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<EmployeeCategory>       getByCategoryId(int categoryId) {
        sql         = "SELECT * FROM EMPLOYEE_CATEGORY WHERE CATEGORY_ID = ?";
        return getJdbcTemplate().query(sql, setParam(categoryId), this::mapper);
    }

    public boolean                      isEmployee(long employeeChatId){
        sql         = "SELECT count(*) FROM EMPLOYEE_CATEGORY WHERE EMPLOYEE_CHAT_ID = ?";
        int count   = getJdbcTemplate().queryForObject(sql, setParam(employeeChatId), Integer.class);
        if(count > 0) return true;
        return false;
    }

    public boolean                       isEmployee(int categoryId, long employeeChatId){
        sql          = "SELECT count(*) FROM EMPLOYEE_CATEGORY WHERE CATEGORY_ID = ? AND  EMPLOYEE_CHAT_ID = ?";
        int count    = getJdbcTemplate().queryForObject(sql, setParam(categoryId, employeeChatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

    public List<EmployeeCategory>       getAll(long id) {
        sql         = "SELECT * FROM EMPLOYEE_CATEGORY WHERE CATEGORY_ID = ? GROUP BY ID";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public void                         addEmployee(int categoryId, long employeeChatId){
        sql = "INSERT INTO EMPLOYEE_CATEGORY VALUES (DEFAULT, ?, ? )";
        getJdbcTemplate().update(sql, categoryId, employeeChatId);
    }

    public void                         delete(EmployeeCategory employeeCategory){
        sql         = "DELETE FROM EMPLOYEE_CATEGORY WHERE ID = ?";
        getJdbcTemplate().update(sql, employeeCategory.getId());
    }

    public void                         delete(long employeeChatId, int categoryId){
        sql = "DELETE FROM EMPLOYEE_CATEGORY WHERE EMPLOYEE_CHAT_ID = ? AND CATEGORY_ID = ?";
        getJdbcTemplate().update(sql, employeeChatId, categoryId);
    }

    public void                         insert(EmployeeCategory employeeCategory){
        sql = "INSERT INTO EMPLOYEE_CATEGORY(CATEGORY_ID, EMPLOYEE_CHAT_ID) VALUES (?,?)";
        getJdbcTemplate().update(sql, employeeCategory.getCategoryId(), employeeCategory.getEmployeeChatId());
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
