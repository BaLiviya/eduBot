package baliviya.com.github.eduBot.entity.standart;


import lombok.Data;

@Data
public class Staff {
    private int        id;
    private int        positionId;
    private String     fullName;
    private String     photo;
    private String     phone;
    private String     mPhone;
    private int        departmentId;
    private int        menuId;
}
