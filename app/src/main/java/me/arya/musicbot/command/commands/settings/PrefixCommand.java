package me.arya.musicbot.command.commands.settings;

import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;

public class PrefixCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final EmbedMessage embedMessage = new EmbedMessage();
        if (ctx.getArgs().isEmpty()) {
            embedMessage.setDescription("Current **Prefix** is `"+ Config.get("prefix") +"`");
        } else {
            embedMessage.setDescription("Setting **Prefix** to `" + ctx.getArgs().get(0) + "`");
        }

        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getHelp() {
        return "Changes the current prefix";
    }
}
