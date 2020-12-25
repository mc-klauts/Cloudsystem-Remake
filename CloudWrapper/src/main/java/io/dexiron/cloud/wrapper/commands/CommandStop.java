package io.dexiron.cloud.wrapper.commands;

import io.dexiron.cloud.lib.config.command.Command;
import io.dexiron.cloud.wrapper.CloudBootsrap;

public class CommandStop extends Command {
    public CommandStop() {
        super("stop", "shutdown all services");
    }

    @Override
    public void execute(String[] args) {
        CloudBootsrap.getInstance().stopWrapper();
    }
}
