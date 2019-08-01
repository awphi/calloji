package ph.adamw.calloji.server.console.command.core;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(Command command) {
        commands.put(command.getBase(), command);
    }

    public Command getCommand(String arg) {
        return commands.get(arg.toLowerCase());
    }
}
