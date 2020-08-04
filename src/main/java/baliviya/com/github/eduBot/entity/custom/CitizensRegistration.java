package baliviya.com.github.eduBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class CitizensRegistration {

    private int id;
    private long chatId;
    private int receptionId;
    private String fullName;
    private String question;
    private String status;
    private Date date;
    private Date citizensDate;
    private String citizensTime;
}
