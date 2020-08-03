package baliviya.com.github.eduBot.entity.standart;

import baliviya.com.github.eduBot.entity.enums.Language;
import lombok.Data;

@Data
public class Button {

    private int      id;
    private String   name;
    private int      commandId;
    private String   url;
    private Language language;
    private boolean  requestContact;
    private int      messageId;
}
