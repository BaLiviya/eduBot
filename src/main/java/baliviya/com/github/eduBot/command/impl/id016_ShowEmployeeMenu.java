package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.standart.Message;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
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
        Message message = messageDao.getMessage(messageId);
        sendMessage(messageId, chatId, null, message.getPhoto());
        if (message.getFile() != null){
            switch (message.getFileType()){
                case audio:
                    bot.execute(new SendAudio().setAudio(message.getFile()).setChatId(chatId));
                case video:
                    bot.execute(new SendVideo().setVideo(message.getFile()).setChatId(chatId));
                case document:
                    bot.execute(new SendDocument().setDocument(message.getFile()).setChatId(chatId));
            }
        }




        return EXIT;
    }
}
