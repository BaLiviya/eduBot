package baliviya.com.github.eduBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class CitizensInfo {

    private int id;
    private String document;
    private Date date;
    private String time;
}
