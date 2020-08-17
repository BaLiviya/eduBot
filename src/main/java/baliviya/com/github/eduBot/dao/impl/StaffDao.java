package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.standart.Staff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StaffDao extends AbstractDao<Staff> {

    public List<Staff> getByPositionId(int positionId){
        sql = "SELECT * FROM STAFF WHERE POSITION_ID = ?";
        return getJdbcTemplate().query(sql, setParam(positionId), this::mapper);
    }

    public List<Staff> getStaffDepart(int departmentId){
        sql = "SELECT * FROM STAFF WHERE DEPARTMENT_ID = ?";
        return getJdbcTemplate().query(sql, setParam(departmentId), this::mapper);
    }

    public Staff getStaff(int positionId){
        sql = "SELECT * FROM STAFF WHERE POSITION_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(positionId), this::mapper);
    }

    public Staff getStaffDep(int departmentId){
        sql = "SELECT * FROM STAFF WHERE DEPARTMENT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(departmentId), this::mapper);
    }

    public List<Staff> getAll(int departmentId){
        sql = "SELECT * FROM STAFF WHERE  DEPARTMENT_ID = ?";
        return getJdbcTemplate().query(sql, setParam(departmentId), this::mapper);
    }

    public Staff getMenuId(int menuId){
        sql = "SELECT * FROM STAFF WHERE MENU_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(menuId), this::mapper);
    }



    @Override
    protected Staff mapper(ResultSet rs, int index) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getInt(1));
        staff.setPositionId(rs.getInt(2));
        staff.setFullName(rs.getString(3));
        staff.setPhoto(rs.getString(4));
        staff.setPhone(rs.getString(5));
        staff.setMPhone(rs.getString(6));
        staff.setDepartmentId(rs.getInt(7));
        staff.setMenuId(rs.getInt(8));
        return staff;
    }
}
