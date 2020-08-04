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
            case 500:
                return new id500_Map();
            case 501:
                return new id501_ShowEvent();

        }
        return null;
    }
}
