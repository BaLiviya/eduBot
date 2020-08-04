package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Map;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id500_Map extends Command {

    private Map map_coordinate;
    @Override
    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        map_coordinate = factory.getMapDao().getAll();
        botUtils.sendLocation(chatId, map_coordinate);
        return EXIT;
    }
}
