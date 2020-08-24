package baliviya.com.github.eduBot.entity.custom;


import lombok.Data;

@Data
public class TaskArchive {

    private int     id;
    private String  text;
    private int     taskId;
    private String  date;
    private String taskStatus;

}
