package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Staff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StaffDao extends AbstractDao<Staff> {

    public List<Staff>         getAll(){
        sql = "SELECT * FROM STAFF WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    @Override
    protected Staff mapper(ResultSet rs, int index) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getInt(1));
        staff.setName(rs.getString(2));
        staff.setLangId(rs.getInt(3));
        return staff;
    }
}
