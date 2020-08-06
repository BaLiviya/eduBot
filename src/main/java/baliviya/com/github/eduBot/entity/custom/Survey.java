package baliviya.com.github.eduBot.entity.custom;


import lombok.Data;

import java.util.Date;

@Data
public class Survey {

    private int id;
    private long chatId;
    private String answers;
    private Date dateAnswer;
}
