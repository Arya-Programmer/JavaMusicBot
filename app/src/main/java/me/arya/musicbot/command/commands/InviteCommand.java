package me.arya.musicbot.command.commands;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;

public class InviteCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final EmbedMessage embedMessage = new EmbedMessage();

        embedMessage.setDescription(String.format("[invite](%s)", channel.createInvite()));

        channel.sendMessage(embedMessage.build());
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
