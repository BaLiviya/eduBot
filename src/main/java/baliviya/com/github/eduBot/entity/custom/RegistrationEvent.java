package baliviya.com.github.eduBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class RegistrationEvent {

    private int id;
    private long chat_id;
    private long event_id;
    private Date registration_date;
    private boolean isCome;
}
