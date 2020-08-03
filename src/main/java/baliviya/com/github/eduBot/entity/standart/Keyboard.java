package baliviya.com.github.eduBot.entity.standart;

import lombok.Data;

@Data
public class Keyboard {

    private int     id;
    private String  buttonIds;
    private boolean inline;
    private String  comment;
}
