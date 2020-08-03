package baliviya.com.github.eduBot.entity.standart;

import lombok.Data;

@Data
public class User {

    private int    id;
    private long   chatId;
    private String phone;
    private String fullName;
    private String userName;
}
