package baliviya.com.github.eduBot.command;


import baliviya.com.github.eduBot.command.impl.*;
import baliviya.com.github.eduBot.exceptions.NotRealizedMethodException;

public class CommandFactory {

    public static Command getCommand(long id) {
        Command result = getCommandWithoutReflection((int) id);
        if (result == null) throw new NotRealizedMethodException("Not realized for type: " + id);
        return result;
    }

    private static Command getCommandWithoutReflection(int id) {
        switch (id) {
            case 1:
                return new id001_ShowInfo();
            case 2:
                return new id002_SelectionLanguage();
            case 3:
                return new id003_Registration();
            case 4:
                return new id004_Task();
            case 5:
                return new id005_Citizens();
            case 8:
                return new id008_Regulations();
            case 10:
                return new id010_SurveyQuestion();
            case 11:
                return new id011_SurveyReport();
            case 12:
                return new id012_ShowAdminInfo();
            case 13:
                return new id013_EditSurvey();
            case 14:
                return new id014_DoneTask();
            case 15:
                return new id015_NotCompletedTask();
            case 16:
                return new id016_ShowEmployeeMenu();
            case 17:
                return new id017_EditAdmin();
            case 18:
                return new id018_ShowStaffInfo();
            case 500:
                return new id500_Map();
            case 501:
                return new id501_ShowEvent();
            case 502:
                return new id502_UploadFileToHelpLine();
            case 503:
                return new id503_GetFileForHelpLine();
            case 504:
                return new id504_Reminder();
            case 505:
                return new id505_TaskReport();
            case 600:
                return new id600_CompletedAppeal();
            case 601:
                return new id601_NotCompletedAppeal();
            case 602:
                return new id602_InProcessAppeal();
            case 603:
                return new id603_EditEvent();
            case 604:
                return new id604_EditMenu();
            case 20000:
                return new TestSendFile();

        }
        return null;
    }
}
