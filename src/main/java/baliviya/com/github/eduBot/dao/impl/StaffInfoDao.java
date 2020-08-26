package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.StaffInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffInfoDao extends AbstractDao<StaffInfo>  {

    public StaffInfo              getInfo(int staffId){
        sql = "SELECT * FROM STAFF_INFO WHERE STAFF_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(staffId, getLanguage().getId()), this::mapper);
    }

    public void                   staffUpdatePhoto(StaffInfo staffInfo){
        sql = "UPDATE STAFF_INFO SET PHOTO = ? WHERE ID = ? AND STAFF_ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, staffInfo.getPhoto(), staffInfo.getId(), staffInfo.getStaffId(), getLanguage().getId());
    }

    public  void                   staffUpdateText(StaffInfo staffInfo){
        sql = "UPDATE STAFF_INFO SET TEXT = ? WHERE ID = ? AND STAFF_ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, staffInfo.getText(), staffInfo.getId(), staffInfo.getStaffId(), getLanguage().getId());
    }


    @Override
    protected StaffInfo mapper(ResultSet rs, int index) throws SQLException {
        StaffInfo staffInfo = new StaffInfo();
        staffInfo.setId(rs.getInt(1));
        staffInfo.setStaffId(rs.getInt(2));
        staffInfo.setText(rs.getString(3));
        staffInfo.setPhoto(rs.getString(4));
        staffInfo.setLangId(rs.getInt(5));
        return staffInfo;
    }
}
