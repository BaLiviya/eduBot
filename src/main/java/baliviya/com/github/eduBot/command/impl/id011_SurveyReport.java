package baliviya.com.github.eduBot.command.impl;

import baliviya.com.github.eduBot.command.Command;
import baliviya.com.github.eduBot.entity.enums.WaitingType;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.XChart.BarChart;
import baliviya.com.github.eduBot.util.components.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Date;

public class id011_SurveyReport extends Command {


    private DateKeyboard dateKeyboard;
    private Date start;
    private Date end;

    @Override
    public boolean execute() throws TelegramApiException, IOException {
       /* if(!isAdmin()){
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }*/
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                dateKeyboard = new DateKeyboard();
                sendStartDate();
                waitingType = WaitingType.START_DATE;
                return COMEBACK;
            case START_DATE:
                deleteMessage(updateMessageId);
                if(hasCallbackQuery()){
                    if(dateKeyboard.isNext(updateMessageText)){
                        sendStartDate();
                        return COMEBACK;
                    }
                    start = dateKeyboard.getDateDate(updateMessageText);
                    sendEndDate();
                    waitingType = WaitingType.END_DATE;
                }
                return COMEBACK;
            case END_DATE:
                deleteMessage(updateMessageId);
                if(hasCallbackQuery()){
                    if(dateKeyboard.isNext(updateMessageText)){
                        sendStartDate();
                        return COMEBACK;
                    }
                    end = dateKeyboard.getDateDate(updateMessageText);
                    sendReport();
                    waitingType = WaitingType.END_DATE;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int sendStartDate() throws TelegramApiException{
        return toDeleteKeyboard(sendMessageWithKeyboard("Выбирите началную дату, для подробного отчета",
                dateKeyboard.getCalendarKeyboard()));
    }

    private int sendEndDate() throws TelegramApiException{
        return toDeleteKeyboard(sendMessageWithKeyboard("Выберите конечную дату",
                dateKeyboard.getCalendarKeyboard()));
    }

    private void sendReport() throws IOException, TelegramApiException{
        BarChart barChart = new BarChart(bot, chatId , start, end);
        barChart.getChart();
    }
}
