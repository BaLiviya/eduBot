package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Event;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id603_EditEvent extends Command {

    private List<Event> events;
    private Event event;
    private int deleteMessageId;
    private int eventId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {

        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }

        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendEvent();
                waitingType = WaitingType.CHOOSE_EVENT;
                return COMEBACK;
            case CHOOSE_EVENT:
                deleteMessage(deleteMessageId);
                deleteMessage(updateMessageId);
                if (hasMessageText()) {
                    if (isCommand(getText(Const.SLASH_DELETE_MESSAGE))) {
                        eventId         = events.get(getInt()).getId();
                        eventDao        .delete(eventId);
                        sendEvent();
                    } else if (isCommand(getText(Const.SLASH_NEW_MESSAGE))) {
                        deleteMessageId = sendMessage(Const.NEW_NAME_FOR_EVENT_MESSAGE);
                        waitingType = WaitingType.NEW_EVENT;
                    } else if (isCommand(getText(Const.SLASH_SETTING_MESSAGE))) {
                        event           = events.get(getInt());
                        event           .setHide(!event.isHide());
                        eventDao        .updateStatus(event);
                        sendEvent();
                    }
                }
                return COMEBACK;
            case NEW_EVENT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    event               = new Event();
                    event               .setName(updateMessageText);
                    deleteMessageId     = sendMessage(Const.SEND_PHOTO_OR_IMG_EVENT_MESSAGE);
                    waitingType         = WaitingType.SET_PHOTO;
                }
                return COMEBACK;
            case SET_PHOTO:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasPhoto()) {
                    event               .setPhoto(updateMessagePhoto);
                    deleteMessageId     = sendMessage(Const.SEND_INFO_EVENT_MESSAGE);
                    waitingType = WaitingType.SET_TEXT_EVENT;
                }
                return COMEBACK;
            case SET_TEXT_EVENT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    event               .setText(updateMessageText);
                    event               .setHide(false);
                    eventDao            .insert(event);
                    toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.PRESS_DONE_TO_MESSAGE), Const.EVENT_KEYBOARD));
                    waitingType         = WaitingType.CHOOSE_OPTION;
                }
                return COMEBACK;
            case CHOOSE_OPTION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    if (isButton(Const.DONE_BUTTON)){
                        deleteMessage(updateMessageId);
                        sendEvent();
                        waitingType = WaitingType.CHOOSE_EVENT;
                        return COMEBACK;
                    }
                    return EXIT;
                }
        }
        return EXIT;
    }

    private int getInt() {
        return Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
    }

    private boolean isCommand(String command) {
        return updateMessageText.startsWith(command);
    }

    private void sendEvent() throws TelegramApiException {
        String formatMessage            = getText(Const.EVENT_EDIT_INFO_MESSAGE);
        StringBuilder infoByEvent       = new StringBuilder();
        events                          = eventDao.getAll();
        String format                   = getText(Const.EVENT_EDIT_MESSAGE);
        for (int i = 0; i < events.size(); i++) {
            event                       = events.get(i);
            infoByEvent.append(String.format(format, getText(Const.SLASH_DELETE_MESSAGE) + i,
                    getText(Const.SLASH_SETTING_MESSAGE) + i,
                    event.isHide() ? "❌" : "✅", event.getName())).append(next);
        }
        deleteMessageId                 = sendMessage(String.format(formatMessage, infoByEvent.toString(), getText(Const.SLASH_NEW_MESSAGE)));
    }
}
