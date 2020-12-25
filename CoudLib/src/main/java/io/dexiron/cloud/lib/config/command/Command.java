package io.dexiron.cloud.lib.config.command;

public abstract class Command {


    private String name;

    String description;

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void execute(String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
