package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.DepartmentsInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentsInfoDao extends AbstractDao<DepartmentsInfo> {

    public List<DepartmentsInfo>            getDepartmentInfo(int departmentId){
        sql = "SELECT * FROM DEPARTMENTS_INFO WHERE DEPARTMENTS_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(departmentId, getLanguage().getId()), this::mapper);
    }

    public DepartmentsInfo                  getInfo(int positionId, int departmentId){
        sql = "SELECT * FROM DEPARTMENTS_INFO WHERE POSITION_ID = ? AND DEPARTMENTS_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(positionId, departmentId, getLanguage().getId()), this::mapper);
    }

    public void                             depUpdatePhoto(DepartmentsInfo departmentsInfo){
        sql = "UPDATE DEPARTMENTS_INFO SET PHOTO = ? WHERE POSITION_ID = ? AND DEPARTMENTS_ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, departmentsInfo.getPhoto(), departmentsInfo.getPositionId(), departmentsInfo.getDepartmentsId(), getLanguage().getId());
    }

    public void                             depUpdateText(DepartmentsInfo departmentsInfo){
        sql = "UPDATE DEPARTMENTS_INFO SET TEXT = ? WHERE POSITION_ID = ? AND DEPARTMENTS_ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, departmentsInfo.getText(), departmentsInfo.getPositionId(), departmentsInfo.getDepartmentsId(), getLanguage().getId());
    }

    @Override
    protected DepartmentsInfo mapper(ResultSet rs, int index) throws SQLException {
        DepartmentsInfo departmentsInfo = new DepartmentsInfo();
        departmentsInfo.setId(rs.getInt(1));
        departmentsInfo.setDepartmentsId(rs.getInt(2));
        departmentsInfo.setPositionId(rs.getInt(3));
        departmentsInfo.setName(rs.getString(4));
        departmentsInfo.setText(rs.getString(5));
        departmentsInfo.setPhoto(rs.getString(6));
        departmentsInfo.setDocuments(rs.getString(7));
        departmentsInfo.setLangId(rs.getInt(8));
        return departmentsInfo;
    }
}
