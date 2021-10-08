package me.arya.musicbot.command;

import java.util.List;

public interface ICommand {
    void handle(CommandContext ctx);

    String getName();
    String getHelp();

    default List<String> getAliases() {
        return List.of();
    }

    default boolean isPremium() {
        return false;
    }
}
