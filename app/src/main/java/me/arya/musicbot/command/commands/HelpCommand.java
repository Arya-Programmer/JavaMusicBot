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
