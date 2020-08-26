package baliviya.com.github.eduBot.entity.custom;

import lombok.Data;

@Data
public class DepartmentsInfo {
    private int    id;
    private int    departmentsId;
    private int    positionId;
    private String name;
    private String text;
    private String photo;
    private String documents;
    private int    langId;
}
