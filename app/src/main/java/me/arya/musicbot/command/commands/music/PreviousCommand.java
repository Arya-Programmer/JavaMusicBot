package me.arya.musicbot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PreviousCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final EmbedMessage embedMessage = new EmbedMessage();

        if (!selfVoiceState.inVoiceChannel()) {
            embedMessage.setDescription("I need to be in a voice channel for this to work");
            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            embedMessage.setDescription("You need to be in a voice channel for this command to work");
            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel()) && selfVoiceState.getChannel() != null) {
            embedMessage.setDescription("You need to be in a voice channel for this command to work");
            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioTrack audioPlayer = musicManager.audioPlayer.getPlayingTrack();

        final int trackIndex = musicManager.scheduler.loopingQueue.indexOf(audioPlayer);
        if (trackIndex-1 > 0) {
            embedMessage.setDescription("No previous track");
            channel.sendMessage(embedMessage.build()).queue();
        }

        musicManager.scheduler.jumpToTrack(trackIndex-1);
        ctx.getMessage().addReaction("👌").queue();
    }

    @Override
    public String getName() {
        return "previous";
    }

    @Override
    public String getHelp() {
        return "Returns to the previous track";
    }
}
