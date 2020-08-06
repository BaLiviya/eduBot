package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

public class id014_DoneTask extends Command {

    private Task task;
    private boolean statusTask;
    private int deleteMessageId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                if(isButton(20)){
                    String text        = update.getCallbackQuery().getMessage().getText();
                    int id             = Integer.parseInt(text.split(next)[0].replaceAll("[^0-9]",""));
                    task               = factory.getTaskDao().get(id);
                    statusTask         = true;
                    //toDeleteKeyboard(sendMessage(14));
                    deleteMessageId    = sendMessage(14);
                    waitingType        = WaitingType.DONE;
                   // return COMEBACK;
                }
                return COMEBACK;
            case DONE:
                deleteMessage (updateMessageId);
                deleteMessage(deleteMessageId);
                if(hasMessageText()){
                    taskArchiveDao.insert("<b>Выполнено</b>:" + updateMessageText, task.getId());
                    return COMEBACK;
                }
        }
        return EXIT;
    }
}
