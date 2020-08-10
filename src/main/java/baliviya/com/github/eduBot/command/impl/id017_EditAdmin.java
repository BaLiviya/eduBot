package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.standart.User;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Slf4j
public class id017_EditAdmin extends Command {

    private        int           mess;
    private static String        delete;
    private static String        deleteIcon;
    private static String        showIcon;
    private        StringBuilder text;
    private        List<Long>    allAdmins;

    @Override
    public boolean execute()                 throws TelegramApiException   {
        deleteMessage(updateMessageId);
        if(!isAdmin()){
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        if(deleteIcon == null) {
            deleteIcon  = getText(Const.ICON_CROSS);
            showIcon    = getText(Const.ICON_LOUPE);
            delete      = getText(Const.DELETE_BUTTON_SLASH);
        }

        if(mess !=0) deleteMessage(mess);
        if(hasContact()){
            registerNewAdmin();
            return COMEBACK;
        }
        if(updateMessageText.contains(delete)){
            if(allAdmins.size() > 1) {
                int numberAdminList = Integer.parseInt(updateMessageText.replaceAll("[^0-9]",""));
                adminDao.delete(allAdmins.get(numberAdminList));
            }
        }

        sendEditorAdmin();
        return COMEBACK;
    }

    private boolean registerNewAdmin() throws TelegramApiException {
        long newadminChatId    = update.getMessage().getContact().getUserID();
        if(!userDao.isRegistered(newadminChatId)){
            sendMessage(Const.USER_DO_NOT_REGISTERED);
            return EXIT;
        } else {
            if (adminDao.isAdmin(newadminChatId)){
                sendMessage(Const.USER_IS_ADMIN);
                return EXIT;
            } else {
                User user      = userDao.getUserByChatId(newadminChatId);
                adminDao.addAssistant(newadminChatId, String.format("%s %s %s", user.getUserName(), user.getPhone(), DateUtil.getDbMmYyyyHhMmSs(new Date())));
                User userAdmin = userDao.getUserByChatId(chatId);
                log.info("{} added new admin - {}", getInfoByUser(userAdmin), getInfoByUser(user));
                sendEditorAdmin();
            }
        }
        return COMEBACK;
    }

    private void  sendEditorAdmin()  throws TelegramApiException {
        deleteMessage(updateMessageId);
        try{
            getText(true);
            mess = sendMessage(String.format(getText(Const.ADMIN_SHOW_LIST), text.toString()));
        } catch (TelegramApiException e){
            getText(false);
            mess = sendMessage(String.format(getText(Const.ADMIN_SHOW_LIST), text.toString()));
        }
        toDeleteMessage(mess);
    }

    private void  getText(boolean withLink){
        text      = new StringBuilder("");
        allAdmins  = adminDao.getAll();
        int count = 0;
        for(Long admin : allAdmins){
            try {
                User user = userDao.getUserByChatId(admin);
                if (allAdmins.size() == 1) {
                    if (withLink) {
                        text.append(getLinkForUser(user.getChatId(), user.getUserName())).append(space).append(next);
                    } else {
                        text.append(getInfoByUser(user)).append(space).append(next);
                    }
                    text.append(getText(Const.WARNING_INFO_ABOUT_ADMIN)).append(next);
                    count++;
                } else {
                    if (withLink) {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(showIcon).append(getLinkForUser(user.getChatId(),
                                user.getUserName())).append(space).append(next);
                    } else {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(getInfoByUser(user)).append(space).append(next);
                    }
                    count++;
                }
            }catch(Exception e) {e.printStackTrace();}
        }
    }

    private String    getInfoByUser(User user){
        return String.format("%s %s %s", user.getFullName(), user.getPhone(), user.getChatId());
    }

}
