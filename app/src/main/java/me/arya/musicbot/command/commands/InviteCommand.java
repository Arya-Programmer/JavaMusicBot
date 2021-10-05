package me.arya.musicbot.command.commands;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

public class InviteCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final EmbedMessage embedMessage = new EmbedMessage();

        final String invite = channel.getJDA().getInviteUrl();
        embedMessage.setDescription(String.format("[Click here](%s)", invite));

        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getHelp() {
        return "Creates instant invite";
    }
}
