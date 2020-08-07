package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

public class id016_ShowEmployeeMenu extends Command {
    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {

        if(!isEmployee()){
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        deleteMessage(updateMessageId);
        toDeleteKeyboard(sendMessageWithKeyboard(getText(15), 8));



        return EXIT;
    }
}
