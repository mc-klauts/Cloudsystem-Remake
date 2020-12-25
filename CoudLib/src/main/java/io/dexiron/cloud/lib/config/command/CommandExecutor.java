package io.dexiron.cloud.lib.config.command;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {

    private List<Command> commands = Lists.newArrayList();

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public Command getExecutorByName(String name) {
        for (Command command : getCommands()) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public boolean executeLine(String consoleInput) {
        if (consoleInput != null && consoleInput != ""){
            String commandName = consoleInput.split(" ")[0];
            Command executor = getExecutorByName(commandName);

            if (executor == null) {
                return false;
            }

            ArrayList<String> args = new ArrayList<>();
            for (String argument : consoleInput.substring(commandName.length()).split(" ")) {
                if (argument.equalsIgnoreCase("") || argument.equalsIgnoreCase(" ")) {
                    continue;
                }

                args.add(argument);
            }
            executor.execute(args.toArray(new String[args.size()]));
            return true;
        }
        return false;
    }

}
