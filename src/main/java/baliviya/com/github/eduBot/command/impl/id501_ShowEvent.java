package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.custom.Event;
import baliviya.com.github.eduBot.entity.custom.RegistrationEvent;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.ButtonsLeaf;
import baliviya.com.github.eduBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id501_ShowEvent extends Command {

    private List<Event> events;
    private Event event;
    private ButtonsLeaf buttonsLeaf;
    private int deleteMessageId;
    private int secondDeleteMessageId;
    private RegistrationEvent registrationEvent;

    @Override
    public boolean execute() throws TelegramApiException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                events = factory.getEventDao().getAllActive();
                if (events == null || events.size() == 0) {
                    deleteMessageId = sendMessage(Const.ACTION_EVENT_EMPTY);
                    return EXIT;
                }
                List<String> list = new ArrayList<>();
                events.forEach(e -> list.add(e.getName()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.ACTIVE_EVENT_MESSAGE), buttonsLeaf.getListButton()));
                waitingType = WaitingType.EVENT_SELECTION;
                return COMEBACK;
            case EVENT_SELECTION:
                delete();
                if (hasCallbackQuery()) {
                    event = events.get(Integer.parseInt(updateMessageText));
                    String formatMessage = getText(Const.EVENT_INFORMATION_MESSAGE);
                    String result = String.format(formatMessage, event.getName(), event.getText());
                    if (event.getPhoto() != null) {
                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(event.getPhoto())).getMessageId();
                    }
                    deleteMessageId = sendMessageWithKeyboard(result, Const.JOIN_EVENT_KEYBOARD);
                    waitingType = WaitingType.EVENT;
                } else {
                    secondDeleteMessageId = wrongData();
                    toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.ACTIVE_EVENT_MESSAGE), buttonsLeaf.getListButton()));
                }
                return COMEBACK;
            case EVENT:
                delete();
                if (hasCallbackQuery()) {
                    if (isButton(Const.JOIN_EVENT_BUTTON)) {
                        if (!factory.getRegistrationEventDao().isJoinToEvent(chatId, event.getId())) {
                            registrationEvent = new RegistrationEvent();
                            registrationEvent.setChat_id(chatId);
                            registrationEvent.setEvent_id(event.getId());
                            registrationEvent.setRegistration_date(new Date());
                            registrationEvent.setCome(false);
                            factory.getRegistrationEventDao().insert(registrationEvent);
                            deleteMessageId = done();
                            toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.ACTIVE_EVENT_MESSAGE), buttonsLeaf.getListButton()));
                        } else {
                            deleteMessageId = sendMessage(Const.YOU_REGISTERED_MESSAGE);
                            events = factory.getEventDao().getAllActive();
                            if (events == null || events.size() == 0) {
                                deleteMessageId = sendMessage(Const.ACTION_EVENT_EMPTY);
                                return EXIT;
                            }
                            List<String> backList = new ArrayList<>();
                            events.forEach(event1 -> backList.add(event1.getName()));
                            buttonsLeaf = new ButtonsLeaf(backList);
                            toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.ACTIVE_EVENT_MESSAGE), buttonsLeaf.getListButton()));
                            waitingType = WaitingType.EVENT_SELECTION;
                        }
                    } else if (isButton(Const.BACK_BUTTON)) {
                        events = factory.getEventDao().getAllActive();
                        if (events == null || events.size() == 0) {
                            deleteMessageId = sendMessage(Const.ACTION_EVENT_EMPTY);
                            return EXIT;
                        }
                        List<String> backList = new ArrayList<>();
                        events.forEach(e -> backList.add(e.getName()));
                        buttonsLeaf = new ButtonsLeaf(backList);
                        toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.ACTIVE_EVENT_MESSAGE), buttonsLeaf.getListButton()));
                        waitingType = WaitingType.EVENT_SELECTION;
                    }
                } else {
                    String formatMessage = getText(Const.EVENT_INFORMATION_MESSAGE);
                    String result = String.format(formatMessage, event.getName(), event.getText());
                    if (event.getPhoto() != null) {
                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(event.getPhoto())).getMessageId();
                    }
                    deleteMessageId = sendMessageWithKeyboard(result, Const.JOIN_EVENT_KEYBOARD);
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int wrongData() throws TelegramApiException { return botUtils.sendMessage(Const.WRONG_DATA_TEXT, chatId);    }

    private int done() throws TelegramApiException {
        return botUtils.sendMessage(Const.DONE_JOIN_MESSAGE, chatId);
    }

    private void delete() {
        deleteMessage(updateMessageId);
        deleteMessage(deleteMessageId);
        deleteMessage(secondDeleteMessageId);
    }
}
