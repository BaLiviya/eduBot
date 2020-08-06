package baliviya.com.github.eduBot.util;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.KeyboardMarkUpDao;
import baliviya.com.github.eduBot.entity.custom.Map;
import baliviya.com.github.eduBot.entity.enums.ParseMode;
import baliviya.com.github.eduBot.entity.standart.Button;
import baliviya.com.github.eduBot.entity.standart.Message;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class BotUtil {

    private        DefaultAbsSender bot;
    private static DaoFactory factory = DaoFactory.getInstance();

    public                      BotUtil(DefaultAbsSender bot) { this.bot = bot; }

    public void                 deleteMessage(long chatId, int messageId) {
        try {
            bot.execute(new DeleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {}
    }

    public int                  sendMessage(String text, long chatId)                                                           throws TelegramApiException {
        return sendMessage(text, chatId, ParseMode.html);
    }

    public int                  sendMessage(String text, long chatId, ParseMode parseMode)                                      throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(text);
        if (parseMode == baliviya.com.github.eduBot.entity.enums.ParseMode.WITHOUT) {
            sendMessage.setParseMode(null);
        } else {
            sendMessage.setParseMode(parseMode.name());
        }
        return sendMessage(sendMessage);
    }

    public int                  sendMessage(long messageId, long chatId)                                                        throws TelegramApiException {
        return sendMessage(messageId, chatId, null, null);
    }

    public int                  sendMessage(long messageId, long chatId, Contact contact, String photo)                         throws TelegramApiException {
        int result                          = 0;
        Message message                     = factory.getMessageDao().getMessage(messageId);
        SendMessage sendMessage             = new SendMessage().setText(message.getName()).setChatId(chatId).setParseMode(ParseMode.html.name());
        KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
        if (keyboardMarkUpDao.select(message.getKeyboardMarkUpId()) != null) { sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId())); }
        boolean isCaption                   = false;
        if (photo != null) {
            SendPhoto sendPhoto             = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(photo);
            if (message.getName().length() < 200) {
                sendPhoto.setCaption(message.getName());
                isCaption                   = true;
            }
            try {
                result                      = bot.execute(sendPhoto).getMessageId();
            } catch (TelegramApiException e) {
                log.debug("Can't send photo", e);
                isCaption                   = false;
            }
        }
        if (!isCaption) result              = bot.execute(sendMessage).getMessageId();
        if (contact != null) sendContact(chatId, contact);
        return result;
    }

    public int                  sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId)                       throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboard, chatId, 0);
    }

    private int                 sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId, int replyMessageId)   throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.html.name()).setChatId(chatId).setText(text).setReplyMarkup(keyboard);
        if (replyMessageId != 0) sendMessage.setReplyToMessageId(replyMessageId);
        return sendMessage(sendMessage);
    }

    public int                  sendMessage(SendMessage sendMessage)                                                            throws TelegramApiException {
        try {
            return bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(sendMessage.getText() + "\nBad tags");
                return bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }

    public int                  sendContact(long chatId, Contact contact)                                                       throws TelegramApiException {
        return bot.execute(new SendContact().setChatId(chatId).setFirstName(contact.getFirstName()).setLastName(contact.getLastName()).setPhoneNumber(contact.getPhoneNumber())).getMessageId();
    }

    public boolean              hasContactOwner(Update update) {
        return (update.hasMessage() && update.getMessage().hasContact()) && Objects.equals(update.getMessage().getFrom().getId(), update.getMessage().getContact().getUserID());
    }

    /*public void                 sendFileList(long chatIdOperator, List<TFile> fileList) {
        fileList.forEach(x -> {
            try {
                sendFile(x, chatIdOperator);
            } catch (Exception e) {
                log.error("Can't send tFile {}", x.toString());
                log.error("Cause: ", e);
            }
        });
    }

    public int                  sendFile(TFile file, long chatId) {
        try {
            switch (file.getTypeId()) {
                case 1:
                    return this.bot.execute(new SendPhoto().setChatId(chatId).setPhoto(file.getLink())).getMessageId();
                case 2:
                    return this.bot.execute(new SendDocument().setChatId(chatId).setDocument(file.getLink())).getMessageId();
                case 3:
                    return this.bot.execute(new SendVideo().setChatId(chatId).setVideo(file.getLink())).getMessageId();
                case 4:
                    return this.bot.execute(new SendAudio().setChatId(chatId).setAudio(file.getLink())).getMessageId();
                case 5:
                    return this.bot.execute(new SendVoice().setChatId(chatId).setVoice(file.getLink())).getMessageId();
            }
            return this.bot.execute(new SendDocument().setChatId(chatId).setDocument(file.getLink())).getMessageId();
        } catch (TelegramApiException e) {
            log.error("Can't send by userChatId: {} - file:{}", chatId, file.toString());
            log.error("Reason: ", e);
            return -1;
        }
    }*/

    public ReplyKeyboard        addButton(ReplyKeyboard replyKeyboard, Button buttonFromDb) {
        if (replyKeyboard == null) return getInlineKeyboard(new String[] {buttonFromDb.getName()}, new String[]{buttonFromDb.getName()});
        try {
            InlineKeyboardMarkup keyboard        = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> rowButton = new ArrayList<>();
            InlineKeyboardButton button          = new InlineKeyboardButton();
            String buttonText                    = buttonFromDb.getName();
            button.setText(buttonText);
            if (buttonFromDb.getUrl() != null) {
                button.setUrl(buttonFromDb.getUrl());
            } else {
                buttonText                       = (buttonText.length() < 64) ? buttonText : buttonText.substring(0, 64);
                button.setCallbackData(buttonText);
            }
            button.setCallbackData(buttonFromDb.getName());
            rowButton.add(button);
            keyboard.getKeyboard().add(rowButton);
            return keyboard;
        } catch (Exception e) {
            ReplyKeyboardMarkup keyboard        = new ReplyKeyboardMarkup();
            KeyboardRow keyboardRow             = new KeyboardRow();
            KeyboardButton keyboardButton       = new KeyboardButton();
            keyboardButton.setText(buttonFromDb.getName());
            keyboardButton.setRequestContact(buttonFromDb.isRequestContact());
            keyboardRow.add(keyboardButton);
            keyboard.getKeyboard().add(keyboardRow);
            return keyboard;
        }
    }

    public InlineKeyboardMarkup getInlineKeyboard(String[] namesButton, String[] callbackMessage) {
        InlineKeyboardMarkup keyboard                   = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsKeyboard   = new ArrayList<>();
        int callbackIndex = 0;
        for (int i = 0; i < namesButton.length; i++) {
            String buttonIdsString                      = namesButton[i];
            List<InlineKeyboardButton> rowButton        = new ArrayList<>();
            String[] buttonIds                          = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                InlineKeyboardButton button             = new InlineKeyboardButton();
                button.setText(buttonId);
                if (callbackMessage == null) {
                    button.setCallbackData(buttonId);
                } else {
                    button.setCallbackData(callbackMessage[callbackIndex++]);
                }
                rowButton.add(button);
            }
            rowsKeyboard.add(rowButton);
        }
        keyboard.setKeyboard(rowsKeyboard);
        return keyboard;
    }

    public Location sendLocation(long chat_id, Map map) {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(chat_id);
        sendLocation.setLongitude(map.getLongitude());
        sendLocation.setLatitude(map.getLatitude());
        try {
            return bot.execute(sendLocation).getLocation();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

}
