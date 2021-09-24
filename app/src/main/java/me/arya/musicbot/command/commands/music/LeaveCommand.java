package me.arya.musicbot.command.commands.music;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class LeaveCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel()) && selfVoiceState.getChannel() != null) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }
        final Guild guild = ctx.getGuild();

        final AudioManager audioManager = guild.getAudioManager();

        audioManager.closeAudioConnection();

        channel.sendMessage("I have left the voice channel").queue();
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return "Leaves the voice channel";
    }

    @Override
    public List<String> getAliases() {
        return List.of("getout", "fuckoff", "whatareyoudoinghere");
    }
}
