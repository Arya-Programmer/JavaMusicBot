/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot.command.commands;

import me.arya.musicbot.CommandManager;
import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand implements ICommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        final EmbedMessage embedMessage = new EmbedMessage();

        if (args.isEmpty()) {
            embedMessage.setTitle("List of commands");

            manager.getCommands().stream().map(ICommand::getName).forEach(
                    (command) -> embedMessage.appendDescription("`")
                            .appendDescription(Config.get("prefix"))
                            .appendDescription(command)
                            .appendDescription("`\n")
            );

            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null) {
            embedMessage.setDescription("Nothing found for `" + search);
            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        embedMessage.setTitle(search.substring(0, 1).toUpperCase() + search.substring(1));
        embedMessage.setDescription(command.getHelp());
        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows the list of commands\n" +
                "Usage: `"+ Config.get("prefix") + "help [command]`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }
}
