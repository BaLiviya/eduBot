package baliviya.com.github.eduBot.command;


import baliviya.com.github.eduBot.command.impl.id002_SelectionLanguage;
import baliviya.com.github.eduBot.command.impl.id003_Registration;
import baliviya.com.github.eduBot.command.impl.id004_Task;
import baliviya.com.github.eduBot.exceptions.NotRealizedMethodException;

public class CommandFactory {

    public  static Command getCommand(long id) {
        Command result = getCommandWithoutReflection((int) id);
        if (result == null) throw new NotRealizedMethodException("Not realized for type: " + id);
        return result;
    }

    private static Command getCommandWithoutReflection(int id) {
        switch (id) {
            case 1:
//                return new id001_ShowInfo();
            case 2:
                return new id002_SelectionLanguage();
            case 3:
                return new id003_Registration();
            case 4:
                return new id004_Task();

        }
        return null;
    }
}
